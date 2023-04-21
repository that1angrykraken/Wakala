package com.kraken.wakala.repositories;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.kraken.wakala.interfaces.IDataChangeListener;
import com.kraken.wakala.dtos.User;

public class UserDataRepo {
    private static final String TAG = "UserDataRepo";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final IDataChangeListener listener;

    public UserDataRepo(Object object) {
        listener = (IDataChangeListener) object;
    }

    public MutableLiveData<User> getCurrentUser(String userEmail){
        MutableLiveData<User> userData = new MutableLiveData<>();
        db.collection("Users").document(userEmail).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User data = documentSnapshot.toObject(User.class);
                    userData.setValue(data);
                    listener.getData(null);
                })
                .addOnFailureListener(listener::getData);
        return userData;
    }

    public void updateUser(User user){
        db.collection("Users").document(user.getEmail()).set(user)
                .addOnSuccessListener(unused -> listener.updateData(null))
                .addOnFailureListener(listener::updateData);
    }

    public void addUser(User user){
        db.collection("Users").document(user.getEmail()).set(user)
                .addOnSuccessListener(unused -> listener.addData(null))
                .addOnFailureListener(listener::addData);
    }
}
