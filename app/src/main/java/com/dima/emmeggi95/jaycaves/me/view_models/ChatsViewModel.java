package com.dima.emmeggi95.jaycaves.me.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.dima.emmeggi95.jaycaves.me.entities.User;
import com.dima.emmeggi95.jaycaves.me.entities.db.ChatPreview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatsViewModel extends ViewModel {

    private static MutableLiveData<List<ChatPreview>> chats = new MutableLiveData<>();
    private static List<ChatPreview> chatList = new ArrayList<>();

    public ChatsViewModel() {
        // Get chat previews from DB
        // temp
        chatList.addAll(User.chats);
        commit();
    }

    public LiveData<List<ChatPreview>> getData(){
        return chats;
    }

    public void setRead(String email){
        getChat(email).setUnreadMessages_1(0);
        commit();
    }

    public void setLastMessage(String email, long last){
        //getChat(email).setLastMessage(last);
        sortChats();
        commit();
    }

    public void addChatPreview(ChatPreview newChat){
        chatList.add(newChat);
        commit();
    }

    private ChatPreview getChat(String chatId){
        for(ChatPreview c : chatList){
            if(c.getChatId().equals(chatId)){
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
