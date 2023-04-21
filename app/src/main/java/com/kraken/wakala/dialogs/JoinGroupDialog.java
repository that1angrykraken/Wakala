package com.kraken.wakala.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.kraken.wakala.databinding.DialogJoinGroupBinding;
import com.kraken.wakala.dtos.User;
import com.kraken.wakala.interfaces.IDataChangedCallBack;
import com.kraken.wakala.viewmodels.AppViewModelStore;
import com.kraken.wakala.viewmodels.GroupViewModel;
import com.kraken.wakala.viewmodels.UserViewModel;

public class JoinGroupDialog extends Dialog implements IDataChangedCallBack {
    private DialogJoinGroupBinding binding;
    private GroupViewModel groupViewModel;
    private UserViewModel userViewModel;

    public JoinGroupDialog(@NonNull Context context) {
        super(context);
        groupViewModel = new ViewModelProvider(AppViewModelStore::getInstance).get(GroupViewModel.class);
        groupViewModel.setCallBack(this);
        userViewModel = new ViewModelProvider(AppViewModelStore::getInstance).get(UserViewModel.class);
        userViewModel.setCallBack(this);
        binding = DialogJoinGroupBinding.inflate(LayoutInflater.from(context), null, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        InputMethodManager imm = ContextCompat.getSystemService(getContext(), InputMethodManager.class);

        binding.buttonJoin.setOnClickListener(view -> {
            String groupId = binding.textGroupId.getEditText().getText().toString();
            if(groupId.length() != 20){
                binding.textGroupId.setError("Group ID must be 20 characters");
            }else{
                imm.hideSoftInputFromWindow(binding.textGroupId.getEditText().getWindowToken(), 0);
                binding.textGroupId.setError("");
                binding.textGroupId.getEditText().setInputType(InputType.TYPE_NULL);
                binding.buttonJoin.setEnabled(false);
                binding.buttonCloseDialog.setEnabled(false);
                binding.progressIndicator.setVisibility(View.VISIBLE);
                groupViewModel.loadGroupData(groupId);
            }
        });

        binding.buttonCloseDialog.setOnClickListener(view -> cancel());

        setCanceledOnTouchOutside(false);
//        getWindow().setBackgroundDrawableResource(R.drawable.round_bg);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onSuccess(Object object) {
        if(object instanceof String){
            String operationStr = (String) object;
            if(operationStr.equalsIgnoreCase("r")){
                User currentUser = userViewModel.getUser().getValue();
                currentUser.setGroupId(groupViewModel.getGroup().getValue().getId());
                userViewModel.updateAUser(currentUser);
            }
            else if (operationStr.equalsIgnoreCase("u")){
                dismiss();
            }
        }
    }

    @Override
    public void onFailure(Object object) {
        String errMsg = "";
        if(object instanceof Exception){
            Exception e = (Exception) object;
            errMsg = e.getMessage();
        }
        if(object instanceof String) {
            errMsg = (String) object;
            if(errMsg.equalsIgnoreCase("r")) errMsg = "Group with this ID doesn't exist.";
            else if(errMsg.equalsIgnoreCase("u")) errMsg = "Cannot participate in this group.";
        }
        binding.textGroupId.setError(errMsg);
        binding.textGroupId.getEditText().setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        binding.buttonJoin.setEnabled(true);
        binding.buttonCloseDialog.setEnabled(true);
        binding.progressIndicator.setVisibility(View.INVISIBLE);
    }
}
