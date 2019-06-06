package com.dima.emmeggi95.jaycaves.me.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.dima.emmeggi95.jaycaves.me.entities.db.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MessagesViewModel extends ViewModel {

    private String chatId;
    private MutableLiveData<List<Message>> messages = new MutableLiveData<>();
    private List<Message> fullList = new ArrayList<>();
    private List<Message> currentList = new ArrayList<>();
    private static HashMap<String, List<Message>> allChats = new HashMap<>();
    private DatabaseReference msgRef = FirebaseDatabase.getInstance().getReference("messages");
    private DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("chats");

    private final int MESSAGES_TO_DOWNLOAD = 10;
    private int numberOfLoadings;

    public MessagesViewModel(String chatId) {

        this.chatId = chatId;
        numberOfLoadings = 1;
        msgRef.orderByChild("chatId").equalTo(chatId).limitToLast(200).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                fullList.clear();
                for (DataSnapshot d : data) {
                    fullList.add(d.getValue(Message.class));
                }

                Collections.sort(fullList, Message.dateComparator);
                System.out.println(fullList);
                currentList = fullList.subList(Math.max(fullList.size()-(numberOfLoadings*MESSAGES_TO_DOWNLOAD),0) , fullList.size());
                messages.postValue(currentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });



    /*        if(allChats.containsKey(otherId)){
            fullList.addAll(allChats.get(otherId));
            messages.postValue(fullList);
        } else {
            // Get messages from DB
            loadMoreMessages();
        } */
    }

    public LiveData<List<Message>> getData(){
        return messages;
    }

    public void sendMessage(Message message){

        msgRef.push().setValue(message);
     /*   if(true){ // If sending succeded
            fullList.add(0, message);
            allChats.put(otherId, fullList);
            messages.postValue(fullList);
            return true;
        } else {
            return false;
        }*/

    }

    public void loadMoreMessages(){

        numberOfLoadings ++;
        currentList = fullList.subList(Math.max(fullList.size()-(numberOfLoadings*MESSAGES_TO_DOWNLOAD),0) , fullList.size());
        messages.postValue(currentList);

       /* String lastMessage;
        if(fullList.size()>0) {
            lastMessage = fullList.get(fullList.size() - 1).getTime();
        } else {
            lastMessage = "0";
        }
        // temp
        List<Message> temp = new ArrayList<>();
        int index = fullList.size();
        for(int i = 0; i < MESSAGES_TO_DOWNLOAD/5; i++) {
            temp.add(new Message("Message " + (index + 5*i + 1) + " from " + "Emmeggi95", "03/06/19 9:08", "0", "Emmeggi95",chatId));
            temp.add(new Message("Message " + (index + 5*i + 2) + " from " + "Peu", "03/06/19 9:05", "0", "Peu"));
            temp.add(new Message("Message " + (index + 5*i + 3) + " from " + "Peu", "03/06/19 9:05", "0", "Peu"));
            temp.add(new Message("Message " + (index + 5*i + 4) + " from " + "Emmeggi95", "03/06/19 9:02", "0", "Emmeggi95"));
            temp.add(new Message("Message " + (index + 5*i + 5) + " from " + "Peu", "03/06/19 9:00", "0", "Peu"));
        }
        fullList.addAll(temp);
        allChats.put(otherId, fullList);
        messages.postValue(fullList);
        */
    }
}
