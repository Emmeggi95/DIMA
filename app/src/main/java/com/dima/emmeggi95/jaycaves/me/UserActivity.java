package com.dima.emmeggi95.jaycaves.me;

import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.entities.Review;
import com.dima.emmeggi95.jaycaves.me.entities.UserReviewsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    private String username;
    private String cover_id;
    private String email;
    private List<Review> reviews = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private UserReviewsAdapter adapter;
    private NetworkChangeReceiver networkChangeReceiver = null;
    private IntentFilter intentFilter;
    private DatabaseReference coverReference = FirebaseDatabase.getInstance().getReference("preferences");
    private DatabaseReference reviewReference = FirebaseDatabase.getInstance().getReference("reviews");
    private ProgressBar loading;
    private ImageView cover;
    private TextView numberOfReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set contents
        setContentView(R.layout.activity_user);
        loading = findViewById(R.id.loading_user);
        cover = findViewById(R.id.profile_picture);

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
        email = (String) getIntent().getExtras().get("email");

        // USER PROFILE PIC INIT
        if( username!=null){
            username_text.setText(username);
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

       reviewReference.orderByChild("userEmail").equalTo(email).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               reviews.clear();
               Iterable<DataSnapshot> data= dataSnapshot.getChildren();
               for(DataSnapshot d: data){
                    reviews.add(d.getValue(Review.class));
               }
               numberOfReviews.setText(reviews.size()+ " reviews"); // set updated value
               if(reviews.size()>0) {
                   recyclerView = findViewById(R.id.user_reviews_recyclerview);
                   layoutManager = new LinearLayoutManager(getApplicationContext());
                   adapter = new UserReviewsAdapter(getApplicationContext(), reviews);
                   recyclerView.setLayoutManager(layoutManager);
                   recyclerView.setAdapter(adapter);
               } else {
                   TextView message = findViewById(R.id.no_review_message);
                   message.setVisibility(View.VISIBLE);
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

      //  if (getIntent().hasExtra("reviews"))
        //
        // reviews = (List<Review>) getIntent().getExtras().get("reviews");









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
