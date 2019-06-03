package com.dima.emmeggi95.jaycaves.me.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.dima.emmeggi95.jaycaves.me.entities.ChatPreview;
import com.dima.emmeggi95.jaycaves.me.entities.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ChatsViewModel extends ViewModel {

    private static MutableLiveData<List<ChatPreview>> chats = new MutableLiveData<>();
    private static List<ChatPreview> chatList = new ArrayList<>();

    public ChatsViewModel() {
        // Get chat previews from DB
        // temp
        chatList.add(new ChatPreview("pietro@grotti", "Peu", 0, 0));
        commit();
    }

    public LiveData<List<ChatPreview>> getData(){
        return chats;
    }

    public void setRead(String email){
        getChat(email).setUnreadMessages(0);
        commit();
    }

    public void setLastMessage(String email, long last){
        getChat(email).setLastMessage(last);
        sortChats();
        commit();
    }

    public void addChatPreview(String email, String username, long last){
        chatList.add(0, new ChatPreview(email, username, last, 0));
        commit();
    }

    private ChatPreview getChat(String email){
        for(ChatPreview c : chatList){
            if(c.getEmail().equals(email)){
                return c;
            }
        }
        return null;
    }

    private void sortChats(){
        Collections.sort(chatList);
    }

    private void commit(){
        chats.postValue(chatList);
    }


}
