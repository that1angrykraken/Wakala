package com.kraken.wakala.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.kraken.wakala.adapters.GroupMemberListAdapter;
import com.kraken.wakala.databinding.FragmentGroupBinding;
import com.kraken.wakala.interfaces.IDataChangedCallBack;
import com.kraken.wakala.interfaces.ListItemListener;
import com.kraken.wakala.models.Group;
import com.kraken.wakala.models.GroupMember;
import com.kraken.wakala.models.User;
import com.kraken.wakala.viewmodels.GroupViewModel;
import com.kraken.wakala.viewmodels.UserViewModel;
import com.kraken.wakala.viewmodels.AppViewModelStore;

import java.util.ArrayList;

public class GroupFragment extends Fragment implements IDataChangedCallBack, ListItemListener {

    private FragmentGroupBinding binding;
    private UserViewModel userViewModel;
    private GroupViewModel groupViewModel;
    private GroupMemberListAdapter adapter;
    private User currentUserData;
    private Group groupData;

    public GroupFragment() {
    }

    private void init()
    {
        adapter = new GroupMemberListAdapter(this, new ArrayList<>());

        binding.setGroup(null);
        binding.rvGroupMemberList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvGroupMemberList.setAdapter(adapter);

        userViewModel = new ViewModelProvider(AppViewModelStore::getInstance).get(UserViewModel.class);
        currentUserData = userViewModel.getUser().getValue();

        groupViewModel = new ViewModelProvider(AppViewModelStore::getInstance).get(GroupViewModel.class);
        groupViewModel.init(this);
        if(!currentUserData.getGroupId().isEmpty()) {
            groupViewModel.loadGroupData(currentUserData.getGroupId());
        }

        binding.buttonCreateGroup.setOnClickListener(view -> createGroup());
    }

    private void createGroup() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGroupBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        userViewModel.setCallBack(this);
    }

    @Override
    public void onSuccess(Object object) {
        if(object instanceof String){
            String operationStr = (String) object;
            if(operationStr.equalsIgnoreCase("r")){
                if(groupData != null){
                    ArrayList<GroupMember> members = groupViewModel.getMembers().getValue();
                    adapter.setData(members);
                }
                else{
                    groupData = groupViewModel.getGroup().getValue();
                    binding.setGroup(groupData);
                    groupViewModel.loadGroupMemberData(groupData.getId());
                }
                return;
            }
            if(operationStr.equalsIgnoreCase("c")){
                groupViewModel.loadGroupData(currentUserData.getGroupId());
            }
        }
    }

    @Override
    public void onFailure(Object object) {
        if(object instanceof String){
            String msg = "Operation failed!";
            Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemClickListener(View view, Object object, int i) {

    }

    @Override
    public void onItemLongClickListener(View view, Object object, int i) {

    }
}