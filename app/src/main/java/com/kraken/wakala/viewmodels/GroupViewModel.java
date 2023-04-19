package com.kraken.wakala.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kraken.wakala.interfaces.IDataChangeListener;
import com.kraken.wakala.interfaces.IDataChangedCallBack;
import com.kraken.wakala.models.Group;
import com.kraken.wakala.models.GroupMember;
import com.kraken.wakala.repositories.GroupDataRepo;

import java.util.ArrayList;

public class GroupViewModel extends ViewModel implements IDataChangeListener {
    GroupDataRepo repo;
    MutableLiveData<Group> group;
    MutableLiveData<ArrayList<GroupMember>> members;
    IDataChangedCallBack callBack;

    public void init(IDataChangedCallBack callBack) {
        repo = new GroupDataRepo(this);
        group = new MutableLiveData<>();
        members = new MutableLiveData<>();
        this.callBack = callBack;
    }

    public void setCallBack(IDataChangedCallBack callBack) {
        this.callBack = callBack;
    }

    public void loadGroupData(String groupId){
        repo.getGroupById(groupId).observeForever(group -> this.group.setValue(group));
    }

    public void loadGroupMemberData(String groupId){
        repo.getGroupMember(groupId).observeForever(groupMembers -> members.setValue(groupMembers));
    }

    public MutableLiveData<Group> getGroup() {
        return group;
    }

    public MutableLiveData<ArrayList<GroupMember>> getMembers() {
        return members;
    }

    public void deleteGroup(String groupLeaderId, Group group){
        repo.deleteGroup(groupLeaderId, group);
    }

    public void addGroup(Group group){
        repo.addGroup(group);
    }

    @Override
    public void getListData(Boolean success) {
        //not used
    }

    @Override
    public void getData(Object object) {
        if(object == null) callBack.onSuccess("r");
        else callBack.onFailure("r");
    }

    @Override
    public void deleteData(Object object) {
        if(object == null) callBack.onSuccess("d");
        else callBack.onFailure("d");
    }

    @Override
    public void updateData(Object object) {
        if(object != null) callBack.onSuccess("u");
        else callBack.onFailure("u");
    }

    @Override
    public void addData(Object object) {
        if(object != null) callBack.onSuccess("c");
        else callBack.onFailure("c");
    }
}
