package com.kraken.wakala.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kraken.wakala.R;
import com.kraken.wakala.activities.LoginActivity;
import com.kraken.wakala.databinding.FragmentMeBinding;
import com.kraken.wakala.interfaces.IDataChangedCallBack;
import com.kraken.wakala.dtos.User;
import com.kraken.wakala.viewmodels.UserViewModel;
import com.kraken.wakala.viewmodels.AppViewModelStore;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MeFragment extends Fragment implements IDataChangedCallBack {

    private FragmentMeBinding binding;
    private UserViewModel viewModel;
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private User currentUserData;
    private MaterialAutoCompleteTextView genderSelector;

    public MeFragment() {

    }

    private void init(){
        genderSelector = (MaterialAutoCompleteTextView) binding.dropdownGenderSelector.getEditText();
        String[] items = getResources().getStringArray(R.array.gender_items);
        viewModel = new ViewModelProvider(AppViewModelStore::getInstance).get(UserViewModel.class);
        currentUserData = viewModel.getUser().getValue();
        if(currentUserData != null) {
            binding.setCurrentUser(currentUserData);
            genderSelector.setText(items[currentUserData.getGender()], false);
        }
    }

    private TextWatcher getTextWatcher(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(binding.buttonSaveChanges.isEnabled())
                    binding.buttonSaveChanges.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!binding.buttonSaveChanges.isEnabled())
                    binding.buttonSaveChanges.setEnabled(true);
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMeBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.setCallBack(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Picasso.get().load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(binding.imgMeProfilePhoto);

        TextWatcher watcher = getTextWatcher();
        binding.dropdownGenderSelector.getEditText().addTextChangedListener(watcher);
        binding.datePickerDob.getEditText().addTextChangedListener(watcher);
        binding.textFieldEmail.getEditText().addTextChangedListener(watcher);
        binding.textFieldAddress.getEditText().addTextChangedListener(watcher);
        binding.textFieldPhoneNumber.getEditText().addTextChangedListener(watcher);

        binding.buttonIconSettings.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.menu_me_settings_button_options, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()){
                    case R.id.item_settings:

                        break;
                    case R.id.item_sign_out:
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        requireActivity().finish();
                        startActivity(intent);
                        break;
                }
                return true;
            });
            popupMenu.show();
        });
        genderSelector.setOnItemClickListener((adapterView, view, i, l) -> currentUserData.setGender(i));
        binding.datePickerDob.getEditText().setOnClickListener(view -> {
            String dateString = binding.datePickerDob.getEditText().getText().toString();
            Date date = null;
            Calendar calendar = Calendar.getInstance();
            try {
                date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateString);
                calendar.setTime(date);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(R.string.dp_select_date)
                    .setSelection(calendar.getTime().getTime())
                    .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                    .build();
            picker.addOnPositiveButtonClickListener(selection -> {
                String getDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));
                binding.datePickerDob.getEditText().setText(getDate);
            });
            picker.show(getActivity().getSupportFragmentManager(), "MDP");
            picker.addOnPositiveButtonClickListener(selection -> currentUserData.setDob(binding.datePickerDob.getEditText().getText().toString()));
        });
        binding.buttonSaveChanges.setOnClickListener(view -> {
            view.setEnabled(false);
            binding.progressBarLoadData.setVisibility(View.VISIBLE);
            viewModel.updateAUser(currentUserData);
        });
    }

    @Override
    public void onSuccess(Object object) {
        binding.progressBarLoadData.setVisibility(View.INVISIBLE);
        String msg = "";
        if(object instanceof String){
            switch ((String) object){
                case "c":
                    // do nothing
                    break;
                case "r":

                    break;
                case "u":
                    msg = getString(R.string.msg_applied_changes);
                    break;
            }
        }
        if(!msg.isEmpty()){
            Snackbar.make(binding.buttonSaveChanges, msg, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Object object) {
        String message = getString(R.string.msg_operation_failure);
        switch ((String) object){
            case "r":
                message = getString(R.string.msg_read_data_failure);
                break;
            case "u":
                message = getString(R.string.msg_update_data_failure);
                break;
            case "d":
                message = getString(R.string.msg_delete_data_failure);
        }
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }
}