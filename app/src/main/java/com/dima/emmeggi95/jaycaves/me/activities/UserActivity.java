package com.dima.emmeggi95.jaycaves.me.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.entities.db.AccountPreference;
import com.dima.emmeggi95.jaycaves.me.entities.CoverCache;
import com.dima.emmeggi95.jaycaves.me.entities.NetworkChangeReceiver;
import com.dima.emmeggi95.jaycaves.me.entities.db.ChatPreview;
import com.dima.emmeggi95.jaycaves.me.entities.db.Review;
import com.dima.emmeggi95.jaycaves.me.entities.User;
import com.dima.emmeggi95.jaycaves.me.entities.adapters.UserReviewsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Public page of users, show reviews.
 */
public class UserActivity extends AppCompatActivity {

    // Parameters
    private String username;
    private String cover_id;
    private List<Review> reviews = new ArrayList<>();
    private ChatPreview chat;

   // UI elements
    private ProgressBar loading;
    private ImageView cover;
    private TextView numberOfReviews;
    private String userId;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private UserReviewsAdapter adapter;
    private TextView startChat;
    private LinearLayout chatContainer;
    private ProgressBar progressBar;

    // Networking elements
    private NetworkChangeReceiver networkChangeReceiver = null;
    private IntentFilter intentFilter;

    // DB References
    private DatabaseReference coverReference = FirebaseDatabase.getInstance().getReference("preferences");
    private DatabaseReference reviewReference = FirebaseDatabase.getInstance().getReference("reviews");
    private DatabaseReference chatsReference = FirebaseDatabase.getInstance().getReference("chats");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set contents
        setContentView(R.layout.activity_user);
        loading = findViewById(R.id.loading_user);
        cover = findViewById(R.id.profile_picture);
        progressBar = findViewById(R.id.progress_bar_user);

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.user_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Set user info
        TextView username_text = findViewById(R.id.user_name);
        numberOfReviews = findViewById(R.id.number_of_reviews);

        username =  (String) getIntent().getExtras().get("username");

        // Set start chat button
        startChat = findViewById(R.id.chat_message);
        chatContainer = findViewById(R.id.chat_container);
        chatContainer.setVisibility(View.GONE);


        // USER DATA INIT
        if( username!=null){
            username_text.setText(username);
            coverReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> data= dataSnapshot.getChildren();
                    for(DataSnapshot d: data){
                        cover_id = d.getValue(AccountPreference.class).getCoverphoto();
                        userId=d.getKey();
                    }

                    // fetch cover
                    CoverCache.retrieveCover(cover_id,cover, loading,
                            getApplicationContext().getDir(CoverCache.INTERNAL_DIRECTORY_USERS,MODE_PRIVATE));

                    if (!userId.equals(User.uid))
                        initChat();



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
       }

       reviewReference.orderByChild("author").equalTo(username).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               progressBar.setVisibility(View.GONE);
               reviews.clear();
               Iterable<DataSnapshot> data= dataSnapshot.getChildren();
               for(DataSnapshot d: data){
                    reviews.add(d.getValue(Review.class));
               }
               numberOfReviews.setText(reviews.size()+ " reviews"); // set updated value
               if(reviews.size()>0) {
                   recyclerView = findViewById(R.id.user_reviews_recyclerview);
                   layoutManager = new LinearLayoutManager(UserActivity.this);
                   adapter = new UserReviewsAdapter(UserActivity.this, reviews);

                   DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
                   recyclerView.addItemDecoration(dividerItemDecoration);

                   recyclerView.setLayoutManager(layoutManager);
                   recyclerView.setAdapter(adapter);
               } else {
                   TextView message = findViewById(R.id.no_review_message);
                   message.setVisibility(View.VISIBLE);
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {}
       });


    }


    private void initChat(){

        chat = new ChatPreview();
        boolean existingChat = false;

        for (ChatPreview chatPreview : User.chats)
            if (chatPreview.getUser_1().equals(userId) || chatPreview.getUser_2().equals(userId)) {
                chat = chatPreview;
                existingChat = true;
            }


        if (!existingChat){

            // NEW CHAT
            chatContainer.setVisibility(View.VISIBLE);
            startChat.setText(getString(R.string.chat_with_this_user) + " " + username);
            chat = new ChatPreview(User.uid + "_" + userId, User.uid, userId,0,0);

        } else {

            // EXISTING CHAT
            chatContainer.setVisibility(View.VISIBLE);
            startChat.setText(getString(R.string.open_chat_user) + " " + username);

        }

        chatContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatsReference.child(chat.getChatId()).setValue(chat);
                Intent intent = new Intent(UserActivity.this, ChatActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("chatId", chat.getChatId());
                intent.putExtra("cover", cover_id);
                startActivity(intent);

            }
        });

    }


    // OVERRIDE ON ACTIVITY METHODS

    @Override
    protected void onPause() {
        super.onPause();
        if(this.networkChangeReceiver!=null) {
            unregisterReceiver(this.networkChangeReceiver);
        }

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

    }
}
