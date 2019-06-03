package com.dima.emmeggi95.jaycaves.me.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.dima.emmeggi95.jaycaves.me.entities.ChatPreview;
import com.dima.emmeggi95.jaycaves.me.entities.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatsViewModel extends ViewModel {

    private static MutableLiveData<List<ChatPreview>> chats = new MutableLiveData<>();
    private static List<ChatPreview> chatList = new ArrayList<>();

    public ChatsViewModel() {
        // Get chat previews from DB
        // temp
        chatList.add(new ChatPreview("pietro@grotti", "Peu", "11/07/2018", 0));
        chats.postValue(chatList);
    }

    public LiveData<List<ChatPreview>> getData(){
        return chats;
    }


}
