package com.kraken.wakala.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.kraken.wakala.interfaces.IDataChangedCallBack;
import com.kraken.wakala.models.User;
import com.kraken.wakala.interfaces.IDataChangeListener;
import com.kraken.wakala.repositories.UserDataRepo;

import java.util.ArrayList;
import java.util.List;

public class UserViewModel extends ViewModel implements IDataChangeListener {
    UserDataRepo repo;
    MutableLiveData<ArrayList<User>> data;
    IDataChangedCallBack callBack;

    public UserViewModel() {
    }

    public void loadData(){
        repo = new UserDataRepo(this);
        data = new MutableLiveData<>();
        repo.getUserData().observeForever(users -> data.setValue(users));
    }

    public void setCallBack(IDataChangedCallBack callBack) {
        this.callBack = callBack;
    }

    public MutableLiveData<ArrayList<User>> getData() {
        return data;
    }

    public void deleteUser(User u1, User u2){
        repo.deleteUser(u1,u2);
    }

    public User getUser(String userEmail){
        User result = null;
        for(User user : data.getValue()){
            if(userEmail.equalsIgnoreCase(user.getEmail())){
                result = user;
                break;
            }
        }
        return result;
    }

    public void addAUser(User user){
        repo.addUser(user);
    }

    public void updateAUser(User user){
        repo.updateUser(user);
    }

    @Override
    public void getListData(Boolean success) {
        if(success) callBack.onSuccess(true);
        else callBack.onFailure("r");
    }

    @Override
    public void getData(Object object) {

    }

    @Override
    public void deleteData(Object object) {
        if(object != null) callBack.onSuccess(object);
        else callBack.onFailure("d");
    }

    @Override
    public void updateData(Object object) {
        if(object != null) callBack.onSuccess(object);
        else callBack.onFailure("u");
    }

    @Override
    public void addData(Object object) {
        if(object != null) callBack.onSuccess(object);
        else callBack.onFailure("c");
    }
}
