package com.dima.emmeggi95.jaycaves.me;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.entities.HomeAlbumsAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class ArtistActivity extends AppCompatActivity {

    Artist artist;
    FirebaseStorage storage;
    StorageReference storageReference;
    File localFile;
    ImageView cover;

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

            cover= findViewById(R.id.imageView2);
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
    }

    private void setImage(ImageView cover, File file){
        String filePath= file.getPath();
        Bitmap map = BitmapFactory.decodeFile(filePath);
        cover.setImageBitmap(map);
    }
}
