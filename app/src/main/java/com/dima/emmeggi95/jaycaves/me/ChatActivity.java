package com.dima.emmeggi95.jaycaves.me;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Handler;
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

import com.dima.emmeggi95.jaycaves.me.entities.ChatAdapter;
import com.dima.emmeggi95.jaycaves.me.entities.Message;
import com.dima.emmeggi95.jaycaves.me.view_models.MessagesViewModel;
import com.dima.emmeggi95.jaycaves.me.view_models.MessagesViewModelFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private String username, email;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ChatAdapter adapter;

    private MessagesViewModel viewModel;
    private List<Message> messages;

    private boolean isLoading = false;
    private final int ITEMS_TO_LOAD = 10;

    private ProgressBar loading;
    private ImageView cover;
    private String cover_id;

    // DB
    private DatabaseReference coverReference = FirebaseDatabase.getInstance().getReference("preferences");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");

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
        if( username!=null){
            coverReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> data= dataSnapshot.getChildren();
                    for(DataSnapshot d: data){
                        cover_id = d.getValue(AccountPreference.class).getCoverphoto();
                    }
                    CoverCache.retrieveCover(cover_id,cover, loading,
                            getApplicationContext().getDir(CoverCache.INTERNAL_DIRECTORY_USERS,MODE_PRIVATE));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        // Get messages
        messages = new ArrayList<>();
        viewModel = ViewModelProviders.of(this, new MessagesViewModelFactory(email)).get(MessagesViewModel.class);

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
                SimpleDateFormat sdt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Message newMessage = new Message(input.getText().toString(), sdt.format(currenTime), User.uid, User.username);
                // Send message to Firebase
                viewModel.sendMessage(newMessage);
                input.setText("");
            }
        });

        recyclerView.scrollToPosition(0);

    }
}
