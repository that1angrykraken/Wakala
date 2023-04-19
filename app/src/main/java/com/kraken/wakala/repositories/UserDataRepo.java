package com.kraken.wakala.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.kraken.wakala.interfaces.IDataChangeListener;
import com.kraken.wakala.models.User;

import java.util.ArrayList;
import java.util.List;

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
