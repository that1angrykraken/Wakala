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

    public MutableLiveData<ArrayList<User>> getUserData(){
        MutableLiveData<ArrayList<User>> data = new MutableLiveData<>();
        db.collection("Users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<User> users = new ArrayList<>();
                    for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                        User user = document.toObject(User.class);
                        user.setId(document.getId());
                        users.add(user);
                    }
                    data.setValue(users);
                    listener.getListData(true);
                })
                .addOnFailureListener(e -> {
                    listener.getListData(false);
                    Log.e(TAG, "getUserData: ", e);
                });
        return data;
    }

    public void deleteUser(User currentUser, User userToRemove){
        db.collection("Users").document(currentUser.getId()+"/friends/"+userToRemove.getId()).delete()
                .addOnSuccessListener(unused -> listener.deleteData(userToRemove))
                .addOnFailureListener(e -> listener.deleteData(null));
    }

    public void updateUser(User user){
        db.collection("Users").document(user.getId()).set(user)
                .addOnSuccessListener(unused -> listener.deleteData(user))
                .addOnFailureListener(e -> listener.deleteData(null));
    }

    public void addUser(User user){
        db.collection("Users").add(user)
                .addOnSuccessListener(unused -> listener.deleteData(user))
                .addOnFailureListener(e -> listener.deleteData(null));
    }
}
