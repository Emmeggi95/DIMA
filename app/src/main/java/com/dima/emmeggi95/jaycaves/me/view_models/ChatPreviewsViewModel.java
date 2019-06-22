package com.dima.emmeggi95.jaycaves.me.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.dima.emmeggi95.jaycaves.me.entities.NotificationLike;
import com.dima.emmeggi95.jaycaves.me.entities.User;
import com.dima.emmeggi95.jaycaves.me.entities.db.AccountPreference;
import com.dima.emmeggi95.jaycaves.me.entities.db.Album;
import com.dima.emmeggi95.jaycaves.me.entities.db.ChatPreview;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatPreviewsViewModel extends ViewModel {

    private static MutableLiveData<List<ChatPreview>> previews = new MutableLiveData<>();
    private static List<ChatPreview> mychats = new ArrayList<>();
    private static List<ChatPreview> otherchats = new ArrayList<>();
    private static List<ChatPreview> chats = new ArrayList<>();
    private static DatabaseReference preferenceRef = FirebaseDatabase.getInstance().getReference("preferences");
    private FirebaseDatabase database;

    public ChatPreviewsViewModel() {
        database = FirebaseDatabase.getInstance();
        DatabaseReference chatsRef = database.getReference("chats");
        chatsRef.orderByChild("user_1").equalTo(User.uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                mychats.clear();
                for (DataSnapshot d : data) {
                    ChatPreview m_chat = d.getValue(ChatPreview.class);
                    mychats.add(m_chat);
                    if (User.friends.get(m_chat.getChatId()) == null) { // not registered friend
                        preferenceRef.orderByKey().equalTo(m_chat.getUser_1()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                                ArrayList<String> info = new ArrayList<>();
                                for (DataSnapshot d : data) {
                                    AccountPreference pref = d.getValue(AccountPreference.class);
                                    info.add(pref.getUsername());
                                    info.add(pref.getCoverphoto());
                                    User.friends.put(d.getKey(), info);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }



                }
                chats.clear();
                chats.addAll(mychats);
                chats.addAll(otherchats);
                Collections.sort(chats, ChatPreview.dateComparator);
                previews.postValue(chats);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        // OTHER CHATS
        chatsRef.orderByChild("user_2").equalTo(User.uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                otherchats.clear();
                for (DataSnapshot d : data) {
                    ChatPreview o_chat = d.getValue(ChatPreview.class);
                    otherchats.add(o_chat);
                    if (User.friends.get(o_chat.getChatId()) == null) { // not registered friend
                        preferenceRef.orderByKey().equalTo(o_chat.getUser_1()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                                ArrayList<String> info = new ArrayList<>();
                                for (DataSnapshot d : data) {
                                    AccountPreference pref = d.getValue(AccountPreference.class);
                                    info.add(pref.getUsername());
                                    info.add(pref.getCoverphoto());
                                    User.friends.put(d.getKey(), info);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }

                }
                chats.clear();
                chats.addAll(mychats);
                chats.addAll(otherchats);
                Collections.sort(chats, ChatPreview.dateComparator);
                previews.postValue(chats);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public LiveData<List<ChatPreview>> getData(){
        return previews;
    }
}
