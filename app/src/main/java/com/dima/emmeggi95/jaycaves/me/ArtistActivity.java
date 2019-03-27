package com.dima.emmeggi95.jaycaves.me;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.entities.ArtistAlbumsAdapter;

import java.util.ArrayList;
import java.util.List;

import com.dima.emmeggi95.jaycaves.me.layout.GridSpacingItemDecoration;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class ArtistActivity extends AppCompatActivity {

    Artist artist;
    List<Album> albums;
    ImageView cover;
    ProgressBar loading;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                finish();
            }
        });

        // Get artist from intent
        artist = (Artist) getIntent().getSerializableExtra("artist");

        // Set artist attributes
        loading= findViewById(R.id.loading_artist);
        cover= findViewById(R.id.artist_cover);
        setTitle(artist.getName());
        TextView birthdayText = (TextView) findViewById(R.id.birthday_text);
        TextView descriptionText = (TextView) findViewById(R.id.artist_description_text);
        TextView genre1Text = (TextView) findViewById(R.id.genre1_text);
        birthdayText.setText(artist.getBirthdate());
        descriptionText.setText(artist.getStory());
        genre1Text.setText(artist.getGenre1());
        if(artist.getGenre2()!=null){
            TextView genre2Text = (TextView) findViewById(R.id.genre2_text);
            genre2Text.setText(artist.getGenre2());
            genre2Text.setVisibility(View.VISIBLE);
        }
        if(artist.getGenre3()!=null){
            TextView genre3Text = (TextView) findViewById(R.id.genre3_text);
            genre3Text.setText(artist.getGenre3());
            genre3Text.setVisibility(View.VISIBLE);
        }

        // Retrieve cover from cache
        CoverCache.retrieveCover(artist.getCover(), cover,loading, getApplicationContext().getDir(CoverCache.INTERNAL_DIRECTORY_ARTIST,MODE_PRIVATE));

        // Get color from artist cover and set it to the UI elements
        BitmapDrawable drawable;
        Bitmap bitmap; //= BitmapFactory.decodeResource(getResources(), R.drawable.default_cover);
       // if (cover.getVisibility() == View.VISIBLE) {
            drawable = (BitmapDrawable) cover.getDrawable();
            bitmap = drawable.getBitmap();
       // }
        Palette p = Palette.from(bitmap).generate();
        int color;
        if(p.getMutedSwatch()!=null) {
            color = p.getMutedSwatch().getRgb();
        } else {
            color = R.color.colorAccent;
        }
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.artist_toolbar_layout);
        toolbarLayout.setBackgroundColor(color);
        toolbarLayout.setStatusBarScrimColor(color);


        initAlbums();

    }


    /**
     *
     */
    private void initAlbums() {

        albums= new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("albums").orderByChild("artist").equalTo(artist.getName()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for(DataSnapshot d : data){
                    // Add album to list
                    albums.add(d.getValue(Album.class));

                }
                System.out.println(albums);
                // Set album list
                recyclerView = findViewById(R.id.artist_albums_recyclerview);
                layoutManager = new GridLayoutManager(getApplicationContext(), 2);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, getResources().getDimensionPixelSize(R.dimen.layout_margin), true));
                adapter = new ArtistAlbumsAdapter(getApplicationContext(), albums);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("DATABASE DENIED DOWNLOAD");
            }
        });

    }

}
