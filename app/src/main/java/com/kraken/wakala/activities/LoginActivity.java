package com.kraken.wakala.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kraken.wakala.R;
import com.kraken.wakala.databinding.ActivityLoginBinding;
import com.kraken.wakala.interfaces.IDataChangedCallBack;
import com.kraken.wakala.models.User;
import com.kraken.wakala.viewmodels.UserViewModel;

public class LoginActivity extends AppCompatActivity implements IDataChangedCallBack {

    private ActivityLoginBinding binding;
    private UserViewModel userViewModel;

    private static final String TAG = "LoginActivity";
    ActivityResultLauncher<IntentSenderRequest> mGetContent;
    SignInClient oneTapClient;
    BeginSignInRequest signInRequest;
    FirebaseAuth mAuth;

    private void UpdateUI(int result){
        if(result == 1){
            binding.textSignInState.setText(getString(R.string.text_sign_in_successful)
                    .replace("%UserDisplayName%", mAuth.getCurrentUser().getDisplayName()));
            binding.buttonSignIn.setVisibility(View.GONE);
            userViewModel.loadData();
            return;
        }
        binding.textSignInState.setText(getString(R.string.text_sign_in_failed));
        binding.buttonSignIn.setVisibility(View.VISIBLE);
    }

    private void LoadData(){

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            UpdateUI(1);
        }
        else{
            googleSignIn();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.setCallBack(this);

        binding.buttonSignIn.setOnClickListener(v->googleSignIn());
        InitializeLoginComponents();
    }

    private void InitializeLoginComponents(){
        mAuth = FirebaseAuth.getInstance();
        mGetContent = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
            if(result.getResultCode() == RESULT_OK){
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                    String idToken = credential.getGoogleIdToken();
                    if (idToken !=  null) {
                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                        mAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this, task -> {
                                    if (task.isSuccessful()) {
                                        UpdateUI(1);
                                        Log.d(TAG, "signInWithCredential:success");
                                    } else {
                                        UpdateUI(0);
                                        binding.textSignInState.setText(getString(R.string.text_sign_in_failed));
                                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                                    }
                                });
                    }
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case CommonStatusCodes.CANCELED:
                            binding.textSignInState.setText(getString(R.string.text_sign_in_canceled));
                            UpdateUI(0);
                            Log.d(TAG, "One-tap dialog was closed.");
                            break;
                        case CommonStatusCodes.NETWORK_ERROR:
                            binding.textSignInState.setText(getString(R.string.text_network_error));
                            UpdateUI(0);
                            Log.d(TAG, "One-tap encountered a network error.");
                            break;
                        default:
                            binding.textSignInState.setText(getString(R.string.text_sign_in_failed));
                            UpdateUI(0);
                            Log.d(TAG, e.getLocalizedMessage());
                            break;
                    }
                }
            }
        });
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();
    }

    private void googleSignIn(){
        binding.buttonSignIn.setVisibility(View.INVISIBLE);
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        IntentSender sender = result.getPendingIntent().getIntentSender();
                        IntentSenderRequest request = new IntentSenderRequest.Builder(sender).build();
                        mGetContent.launch(request);
                    } catch (ActivityNotFoundException e) {
                        binding.textSignInState.setText(getString(R.string.text_sign_in_failed));
                        UpdateUI(0);
                        Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                    }
                })
                .addOnFailureListener(this, e -> {
                    binding.textSignInState.setText(getString(R.string.text_sign_in_failed));
                    UpdateUI(0);
                    Log.d(TAG, e.getLocalizedMessage());
                });
    }

    @Override
    public void onSuccess(Object object) {
        if(object instanceof Boolean){
            if(userViewModel.getUser(mAuth.getCurrentUser().getEmail()) == null){
                User user = new User();
                user.setEmail(mAuth.getCurrentUser().getEmail());
                user.setName(mAuth.getCurrentUser().getDisplayName());
                user.setDob("01/01/2023");
                userViewModel.addAUser(user);
                return;
            }
        }
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFailure(Object object) {
        if(((String) object).equalsIgnoreCase("r")) {
            mAuth.signOut();
        }
        UpdateUI(0);
    }
}