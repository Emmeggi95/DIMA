package com.dima.emmeggi95.jaycaves.me;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.entities.Review;
import com.dima.emmeggi95.jaycaves.me.entities.UserReference;
import com.dima.emmeggi95.jaycaves.me.entities.UserReviewsAdapter;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    private UserReference user;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private UserReviewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Get user from intent
        if(getIntent().hasExtra("user")){
            user = (UserReference) getIntent().getExtras().get("user");
        } else {
            List<Review> reviews = new ArrayList<>();
            reviews.add(new Review("Generic User", "Generic Album", "This is a review.", "Yadda yadda yadda...", 4.5, "2019-01-01", "pietro@grotti"));
            user = new UserReference("Username", "pietro@grotti", BitmapFactory.decodeResource(getResources(), R.drawable.default_artist), reviews);
        }

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
        TextView username = findViewById(R.id.user_name);
        TextView numberOfReviews = findViewById(R.id.number_of_reviews);
        ImageView profilePicture = findViewById(R.id.profile_picture);
        username.setText(user.getUsername());
        numberOfReviews.setText(user.getReviews().size() + " reviews");
        if(user.getCover()!=null) profilePicture.setImageBitmap(user.getCover());

        if(user.getReviews().size()>0) {
            recyclerView = findViewById(R.id.user_reviews_recyclerview);
            layoutManager = new LinearLayoutManager(this);
            adapter = new UserReviewsAdapter(this, user.getReviews());

            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        } else {
            TextView message = findViewById(R.id.no_review_message);
            message.setVisibility(View.VISIBLE);
        }


    }
}
