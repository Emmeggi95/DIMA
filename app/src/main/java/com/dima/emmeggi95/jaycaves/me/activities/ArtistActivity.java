package com.dima.emmeggi95.jaycaves.me.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.entities.db.Album;
import com.dima.emmeggi95.jaycaves.me.entities.db.Artist;
import com.dima.emmeggi95.jaycaves.me.entities.adapters.ArtistAlbumsAdapter;

import java.util.ArrayList;
import java.util.List;

import com.dima.emmeggi95.jaycaves.me.entities.CoverCache;
import com.dima.emmeggi95.jaycaves.me.entities.NetworkChangeReceiver;
import com.dima.emmeggi95.jaycaves.me.layout.GridSpacingItemDecoration;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Activity to display info related to a certain artist fetched from the system
 */
public class ArtistActivity extends AppCompatActivity {

    Artist artist;
    List<Album> albums;
    ImageView cover;
    ProgressBar loading;
    ProgressBar progressBar;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private NetworkChangeReceiver networkChangeReceiver = null;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.playlist_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

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

        TextView artistName = findViewById(R.id.artist_name);
        TextView birthdayText = (TextView) findViewById(R.id.birthday_text);
        TextView descriptionText = (TextView) findViewById(R.id.artist_description_text);
        TextView genre1Text = (TextView) findViewById(R.id.genre1_text);
        artistName.setText(artist.getName());
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
        if (artist.getCover()!=null && artist.getCover()!="") {
            CoverCache.retrieveCover(artist.getCover(), cover,loading, getApplicationContext().getDir(CoverCache.INTERNAL_DIRECTORY_ARTIST,MODE_PRIVATE));
        }

        // Get color from artist cover and set it to the UI elements
        BitmapDrawable drawable;
        Bitmap bitmap; //= BitmapFactory.decodeResource(getResources(), R.drawable.default_cover);
       // if (cover.getVisibility() == View.VISIBLE) {
            drawable = (BitmapDrawable) cover.getDrawable();
            bitmap = drawable.getBitmap();
       // }
        Palette p = Palette.from(bitmap).generate();
        int color = p.getMutedColor(getResources().getColor(R.color.colorSecondaryLight));
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.artist_toolbar_layout);
        toolbarLayout.setContentScrimColor(color);
        toolbarLayout.setStatusBarScrimColor(color);

        //Set toolbar navigation icon color
        if(isTopLeftDark(bitmap)){
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorTextOnDarkBackground), PorterDuff.Mode.SRC_ATOP);
        } else {
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorTextOnLightBackground), PorterDuff.Mode.SRC_ATOP);
        }

        NestedScrollView container = findViewById(R.id.container);
        container.setBackgroundColor(p.getLightMutedColor(getResources().getColor(R.color.default_background_gradient)));

        progressBar = findViewById(R.id.progress_bar);
        initAlbums();

    }


    /**
     * Fetches and displays every album released by a certain artist
     */
    private void initAlbums() {

        albums= new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("albums").orderByChild("artist").equalTo(artist.getName()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for(DataSnapshot d : data){
                    // Add album to list
                    albums.add(d.getValue(Album.class));

                }
                //System.out.println(albums);
                // Set album list
                if(albums.size()>0) {
                    recyclerView = findViewById(R.id.artist_albums_recyclerview);
                    layoutManager = new GridLayoutManager(ArtistActivity.this, 2);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, getResources().getDimensionPixelSize(R.dimen.layout_margin), true));
                    adapter = new ArtistAlbumsAdapter(ArtistActivity.this, albums);
                    recyclerView.setAdapter(adapter);
                } else {
                    ConstraintLayout noAlbumArea = findViewById(R.id.no_album_area);
                    noAlbumArea.setVisibility(View.VISIBLE);
                    LinearLayout newAlbumArea = findViewById(R.id.new_content);
                    newAlbumArea.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), AddContentActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("DATABASE DENIED DOWNLOAD");
            }
        });

    }

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

    public static boolean isTopLeftDark(Bitmap bitmap){
        boolean dark=false;
        int height = bitmap.getHeight()/8;
        int width = bitmap.getWidth()/8;

        float darkThreshold = width*height*0.45f;
        int darkPixels=0;

        int[] pixels = new int[width*height];
        bitmap.getPixels(pixels,0,width,0,0,width,height);

        for(int pixel : pixels){
            int color = pixel;
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            double luminance = (0.299*r+0.0f + 0.587*g+0.0f + 0.114*b+0.0f);
            if (luminance<150) {
                darkPixels++;
            }
        }

        if (darkPixels >= darkThreshold) {
            dark = true;
        }
        return dark;
    }

}
