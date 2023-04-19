package com.kraken.wakala.repositories;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kraken.wakala.interfaces.IDataChangeListener;
import com.kraken.wakala.models.Group;
import com.kraken.wakala.models.GroupMember;

import java.util.ArrayList;

public class GroupDataRepo {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final IDataChangeListener listener;

    public GroupDataRepo(IDataChangeListener listener) {
        this.listener = listener;
    }

    public MutableLiveData<Group> getGroupById(String groupId){
        MutableLiveData<Group> data = new MutableLiveData<>();
        db.document("/Groups/"+groupId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Group group = documentSnapshot.toObject(Group.class);
                    group.setId(documentSnapshot.getId());
                    data.setValue(group);
                    listener.getData(null);
                })
                .addOnFailureListener(listener::getData);
        return data;
    }

    public MutableLiveData<ArrayList<GroupMember>> getGroupMember(String groupId){
        MutableLiveData<ArrayList<GroupMember>> data = new MutableLiveData<>();
        db.collection("Users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<GroupMember> members = new ArrayList<>();
                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        GroupMember member = documentSnapshot.toObject(GroupMember.class);
                        if(member.getGroupId().equalsIgnoreCase(groupId)){
                            members.add(member);
                        }
                        data.setValue(members);
                    }
                    listener.getData(null);
                })
                .addOnFailureListener(listener::getData);
        return data;
    }

    public void deleteGroup(String groupLeaderId, Group group){
        if(!groupLeaderId.equalsIgnoreCase(group.getLeader())){
            listener.deleteData(new InterruptedException());
            return;
        }
        db.document("/Groups/"+group.getId()).delete()
                .addOnSuccessListener(unused -> listener.deleteData(null))
                .addOnFailureListener(listener::deleteData);
    }

    public void addGroup(Group group){
        db.collection("Groups").add(group)
                .addOnSuccessListener(documentReference -> listener.addData(null))
                .addOnFailureListener(listener::addData);
    }
}
