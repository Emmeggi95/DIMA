package com.dima.emmeggi95.jaycaves.me.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.entities.NetworkChangeReceiver;
import com.dima.emmeggi95.jaycaves.me.entities.db.AccountPreference;
import com.dima.emmeggi95.jaycaves.me.entities.adapters.ChatAdapter;
import com.dima.emmeggi95.jaycaves.me.entities.CoverCache;
import com.dima.emmeggi95.jaycaves.me.entities.db.ChatPreview;
import com.dima.emmeggi95.jaycaves.me.entities.db.Message;
import com.dima.emmeggi95.jaycaves.me.entities.User;
import com.dima.emmeggi95.jaycaves.me.view_models.MessagesViewModel;
import com.dima.emmeggi95.jaycaves.me.view_models.MessagesViewModelFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    // UI Elements
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ChatAdapter adapter;
    private MessagesViewModel viewModel;
    private List<Message> messages;
    private ProgressBar loading;
    private ImageView cover;
    private String cover_id;
    private String chatId;

    // Attributes
    private String username;
    private ChatPreview chat;


    // Networking elements
    private NetworkChangeReceiver networkChangeReceiver = null;
    private IntentFilter intentFilter;

    // DB
    private DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("chats");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        username = getIntent().getStringExtra("username");
        chatId = getIntent().getStringExtra("chatId");
        cover_id = getIntent().getStringExtra("cover");

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        TextView title = findViewById(R.id.user_name);
        title.setText(username);

        // Set user picture
        loading = findViewById(R.id.progressBar);
        cover = findViewById(R.id.user_image);

        // Get messages
        messages = new ArrayList<>();
        viewModel = ViewModelProviders.of(this, new MessagesViewModelFactory(chatId)).get(MessagesViewModel.class);

        // Init recycler view and adapters
        recyclerView = findViewById(R.id.recycler_view_chat);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        adapter = new ChatAdapter(this, messages, viewModel);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        final Observer observer = new Observer<List<Message>>() {
            @Override
            public void onChanged(@Nullable List<Message> data) {
                messages.clear();
                messages.addAll(data);
                messages.add(null);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(viewModel.current_position);
            }
        };


        viewModel.getData().observe(this, observer);

        // Set send button
        ImageButton send = findViewById(R.id.send_button);
        final EditText input = findViewById(R.id.message_input);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date currenTime = Calendar.getInstance().getTime();
                SimpleDateFormat sdt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Message newMessage = new Message(input.getText().toString(), sdt.format(currenTime), User.uid, User.username, chatId);
                viewModel.sendMessage(newMessage);
                input.setText("");
            }
        });

        // USER PROFILE PIC INIT
        CoverCache.retrieveCover(cover_id,cover, loading,
                getApplicationContext().getDir(CoverCache.INTERNAL_DIRECTORY_USERS,MODE_PRIVATE));



        recyclerView.scrollToPosition(0);

    }

    // OVERRIDE ON ACTIVITY METHODS

    @Override
    protected void onPause() {
        super.onPause();
        if(this.networkChangeReceiver!=null) {
            unregisterReceiver(this.networkChangeReceiver);
        }

        // SET ALL MESSAGES AS READ
        chatReference.orderByKey().equalTo(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for (DataSnapshot d : data) {
                    chat = d.getValue(ChatPreview.class);
                    if (chat.getUser_1().equalsIgnoreCase(User.uid))
                        chat.setUnreadMessages_1(0);
                    else
                        chat.setUnreadMessages_2(0);
                    chatReference.child(chat.getChatId()).setValue(chat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Create an IntentFilter instance.
        intentFilter = new IntentFilter();

        // Add network connectivity change action.
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        // Set broadcast receiver priority.
        intentFilter.setPriority(100);

        // Create a network change broadcast receiver.
        networkChangeReceiver = new NetworkChangeReceiver();

        // Register the broadcast receiver with the intent filter object.
        registerReceiver(networkChangeReceiver, intentFilter);


        // SET ALL MESSAGES AS READ
        chatReference.orderByKey().equalTo(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for (DataSnapshot d : data) {
                    chat = d.getValue(ChatPreview.class);
                    if (chat.getUser_1().equals(User.uid))
                        chat.setUnreadMessages_1(0);
                    else
                        chat.setUnreadMessages_2(0);
                    chatReference.child(chat.getChatId()).setValue(chat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }
}
