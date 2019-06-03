package com.dima.emmeggi95.jaycaves.me.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.dima.emmeggi95.jaycaves.me.entities.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessagesViewModel extends ViewModel {

    private String email;
    private MutableLiveData<List<Message>> messages = new MutableLiveData<>();
    private List<Message> messageList = new ArrayList<>();
    private static HashMap<String, List<Message>> allChats = new HashMap<>();

    private final int MESSAGES_TO_DOWNLOAD = 10;

    public MessagesViewModel(String email) {
        this.email = email;
        if(allChats.containsKey(email)){
            messageList.addAll(allChats.get(email));
            messages.postValue(messageList);
        } else {
            // Get messages from DB
            downloadMoreMessages();
        }
    }

    public LiveData<List<Message>> getData(){
        return messages;
    }

    public boolean sendMessage(Message message){
        // Send to DB
        if(true){ // If sending succeded
            messageList.add(0, message);
            allChats.put(email, messageList);
            messages.postValue(messageList);
            return true;
        } else {
            return false;
        }
    }

    public void downloadMoreMessages(){
        String lastMessage;
        if(messageList.size()>0) {
            lastMessage = messageList.get(messageList.size() - 1).getTime();
        } else {
            lastMessage = "0";
        }
        // temp
        List<Message> temp = new ArrayList<>();
        int index = messageList.size();
        for(int i = 0; i < MESSAGES_TO_DOWNLOAD/5; i++) {
            temp.add(new Message("Message " + (index + 5*i + 1) + " from " + "Emmeggi95", "03/06/19 9:08", "0", "Emmeggi95"));
            temp.add(new Message("Message " + (index + 5*i + 2) + " from " + "Peu", "03/06/19 9:05", "0", "Peu"));
            temp.add(new Message("Message " + (index + 5*i + 3) + " from " + "Peu", "03/06/19 9:05", "0", "Peu"));
            temp.add(new Message("Message " + (index + 5*i + 4) + " from " + "Emmeggi95", "03/06/19 9:02", "0", "Emmeggi95"));
            temp.add(new Message("Message " + (index + 5*i + 5) + " from " + "Peu", "03/06/19 9:00", "0", "Peu"));
        }
        messageList.addAll(temp);
        allChats.put(email, messageList);
        messages.postValue(messageList);
    }
}
