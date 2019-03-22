package com.dima.emmeggi95.jaycaves.me;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.entities.ArtistAlbumsAdapter;

import java.util.ArrayList;
import java.util.List;

import com.dima.emmeggi95.jaycaves.me.layout.GridSpacingItemDecoration;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class ArtistActivity extends AppCompatActivity {

    Artist artist;
    List<Album> albums;
    FirebaseStorage storage;
    StorageReference storageReference;
    File localFile;
    ImageView cover;

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
        if(artist.getCover()!=null){

            cover= findViewById(R.id.artist_cover);
            storage= FirebaseStorage.getInstance();
            storageReference= storage.getReference("Artist_covers");

            try {
                localFile = File.createTempFile("artist","jpeg");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            storageReference.child(artist.getCover()).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    setImage(cover, localFile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println(e.getMessage());
                }
            });

        }

        // Get color from artist cover and set it to the UI elements
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_cover);
        Palette p = Palette.from(bitmap).generate();
        int color;
        if(p.getMutedSwatch()!=null)
            color = p.getMutedSwatch().getRgb();
        else
            color = R.color.colorAccent;
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.artist_toolbar_layout);
        toolbarLayout.setBackgroundColor(color);
        toolbarLayout.setStatusBarScrimColor(color);

        // Download the list of albums by this artist
        // ...
        albums = new ArrayList<>();
        albums.add(new Album("1st album", "1977", 4.37, "Pietrus", "Gothic Rock", ""));
        albums.add(new Album("2nd album", "1979", 4.12, "Pietrus", "Gothic Rock", ""));
        albums.add(new Album("3rd album", "1981", 3.48, "Pietrus", "Gothic Rock", ""));

        // Set album list
        recyclerView = (RecyclerView) findViewById(R.id.artist_albums_recyclerview);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, getResources().getDimensionPixelSize(R.dimen.layout_margin), true));
        adapter = new ArtistAlbumsAdapter(this, albums);
        recyclerView.setAdapter(adapter);
    }

    private void setImage(ImageView cover, File file){
        String filePath= file.getPath();
        Bitmap map = BitmapFactory.decodeFile(filePath);
        cover.setImageBitmap(map);
    }
}
