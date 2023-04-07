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
import android.widget.AdapterView;
import android.widget.PopupMenu;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kraken.wakala.R;
import com.kraken.wakala.activities.LoginActivity;
import com.kraken.wakala.databinding.FragmentMeBinding;
import com.kraken.wakala.interfaces.IDataChangedCallBack;
import com.kraken.wakala.interfaces.ListItemListener;
import com.kraken.wakala.models.User;
import com.kraken.wakala.viewmodels.UserViewModel;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MeFragment extends Fragment implements IDataChangedCallBack, ListItemListener {

    private FragmentMeBinding binding;
    private UserViewModel viewModel;
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private User userInfo;
    private String[] items;
    private MaterialAutoCompleteTextView genderSelector;

    public MeFragment() {

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

    private Boolean isValid(){
        boolean result = true;
        String gender = String.valueOf(binding.dropdownGenderSelector.getEditText().getText());
        if(gender.length()==0) {
            binding.dropdownGenderSelector.setError("Choose one.");
            result = false;
        }
        String dob = String.valueOf(binding.textFieldEmail.getEditText().getText());
        return result;
    }

    @Override
    public void onItemClickListener(View view, Object object, int i) {

    }

    @Override
    public void onItemLongClickListener(View view, Object object, int i) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMeBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        viewModel.loadData();
        viewModel.setCallBack(this);
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
        genderSelector = (MaterialAutoCompleteTextView) binding.dropdownGenderSelector.getEditText();
        items = getResources().getStringArray(R.array.gender_items);

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
        ((MaterialAutoCompleteTextView) binding.dropdownGenderSelector.getEditText()).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                userInfo.setGender(i);
            }
        });
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
                    .setTitleText(R.string.text_dp_select_date)
                    .setSelection(calendar.getTime().getTime())
                    .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                    .build();
            picker.addOnPositiveButtonClickListener(selection -> {
                String getDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));
                binding.datePickerDob.getEditText().setText(getDate);
            });
            picker.show(getActivity().getSupportFragmentManager(), "MDP");
            picker.addOnPositiveButtonClickListener(selection -> userInfo.setDob(binding.datePickerDob.getEditText().getText().toString()));
        });
        binding.buttonSaveChanges.setOnClickListener(view -> {
            view.setEnabled(false);
            binding.progressBarLoadData.setVisibility(View.VISIBLE);
            viewModel.updateAUser(userInfo);
        });
    }

    @Override
    public void onSuccess(Object object) {
        binding.progressBarLoadData.setVisibility(View.INVISIBLE);
        if(object instanceof Boolean){
            userInfo = viewModel.getUser(currentUser.getEmail());
            binding.setMeInfo(userInfo);
            genderSelector.setText(items[userInfo.getGender()], false);
            return;
        }
        if(object instanceof User){
            Snackbar.make(binding.buttonSaveChanges, getString(R.string.text_msg_applied_changes), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Object object) {
        String message = getString(R.string.text_msg_operation_failure);
        switch ((String) object){
            case "r":
                message = getString(R.string.text_msg_read_data_failure);
                break;
            case "u":
                message = getString(R.string.text_msg_update_data_failure);
                break;
            case "d":
                message = getString(R.string.text_msg_delete_data_failure);
        }
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }
}