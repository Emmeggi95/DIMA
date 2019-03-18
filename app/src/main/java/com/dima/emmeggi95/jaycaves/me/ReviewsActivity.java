package com.dima.emmeggi95.jaycaves.me;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.dima.emmeggi95.jaycaves.me.entities.ReviewsAdapter;

public class ReviewsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ReviewsAdapter adapter;
    Album album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        album = (Album) getIntent().getExtras().get("album");

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.reviews_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(album.getTitle());

        recyclerView = findViewById(R.id.reviews_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Set the adapter
        adapter = new ReviewsAdapter(this, album.getReviews());
        recyclerView.setAdapter(adapter);
    }
}
