package com.kraken.wakala.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kraken.wakala.interfaces.IDataChangedCallBack;
import com.kraken.wakala.models.User;
import com.kraken.wakala.interfaces.IDataChangeListener;
import com.kraken.wakala.repositories.UserDataRepo;

import java.util.ArrayList;
import java.util.List;

public class UserViewModel extends ViewModel implements IDataChangeListener {
    UserDataRepo repo;
    MutableLiveData<User> data;
    IDataChangedCallBack callBack;

    public UserViewModel() {
    }

    public void init(IDataChangedCallBack callBack){
        repo = new UserDataRepo(this);
        data = new MutableLiveData<>();
        this.callBack = callBack;
    }

    public void loadData(String userEmail){
        repo.getCurrentUser(userEmail).observeForever(user -> data.setValue(user));
    }

    public void setCallBack(IDataChangedCallBack callBack) {
        this.callBack = callBack;
    }

    public MutableLiveData<User> getUser() {
        return data;
    }

    public void addAUser(User user){
        repo.addUser(user);
    }

    public void updateAUser(User user){
        repo.updateUser(user);
    }

    @Override
    public void getListData(Boolean success) {

    }

    @Override
    public void getData(Object object) {
        if(object == null) callBack.onSuccess("r");
        else callBack.onFailure("r");
    }

    @Override
    public void deleteData(Object object) {
//        if(object == null) callBack.onSuccess("d");
//        else callBack.onFailure("d");
    }

    @Override
    public void updateData(Object object) {
        if(object == null) callBack.onSuccess("u");
        else callBack.onFailure("u");
    }

    @Override
    public void addData(Object object) {
        if(object == null)
            callBack.onSuccess("c");
        else
            callBack.onFailure("c");
    }
}
