package com.dima.emmeggi95.jaycaves.me.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.dima.emmeggi95.jaycaves.me.entities.db.ChatPreview;
import com.dima.emmeggi95.jaycaves.me.entities.db.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessagesViewModel extends ViewModel {

    private String chatId;
    private ChatPreview chat;
    private MutableLiveData<List<Message>> messages = new MutableLiveData<>();
    private List<Message> fullList = new ArrayList<>();
    private List<Message> currentList = new ArrayList<>();
    private DatabaseReference msgRef = FirebaseDatabase.getInstance().getReference("messages");
    private DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("chats");

    private final int MESSAGES_TO_DOWNLOAD = 10;
    private int numberOfLoadings;
    private int newMessages;
    public int current_position;

    public MessagesViewModel(String chatId) {

        this.chatId = chatId;
        numberOfLoadings = 1;
        newMessages = -1;
        current_position=0;

        chatReference.orderByKey().equalTo(chatId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for (DataSnapshot d : data) {
                    chat = d.getValue(ChatPreview.class);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        msgRef.orderByChild("chatId").equalTo(chatId).limitToLast(200).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                fullList.clear();
                newMessages++;
                if (newMessages > MESSAGES_TO_DOWNLOAD) {
                    numberOfLoadings++;
                    newMessages =0; // reset counter
                }
                for (DataSnapshot d : data) {
                    fullList.add(d.getValue(Message.class));
                }


                Collections.sort(fullList, Message.dateComparator);
                currentList = fullList.subList(0, Math.min(numberOfLoadings*MESSAGES_TO_DOWNLOAD,fullList.size()));
                current_position=0;
                messages.postValue(currentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });




    }

    public LiveData<List<Message>> getData(){
        return messages;
    }

    public void sendMessage(Message message){

        // Insert message in db
        msgRef.push().setValue(message);

        if (chat.getUser_1().equals(message.getSenderId()))
            chat.setUnreadMessages_2(chat.getUnreadMessages_2() + 1);
        else
            chat.setUnreadMessages_1(chat.getUnreadMessages_1() + 1);


        // Update chat info regarding un-read messages
        chat.setLastMessage(message.getText());
        chat.setLastAccess(ServerValue.TIMESTAMP);
        chatReference.child(chatId).setValue(chat);


    }

    public void loadMoreMessages(){

        numberOfLoadings ++;
        currentList = fullList.subList(0, Math.min(numberOfLoadings*MESSAGES_TO_DOWNLOAD,fullList.size()));
        current_position= -1;
        messages.postValue(currentList);

    }
}
