package com.dima.emmeggi95.jaycaves.me;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

public class AlbumActivity extends AppCompatActivity {

    HomeAlbum album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        // Get album from intent extra
        album = (HomeAlbum) getIntent().getSerializableExtra("album");
        Bitmap coverBitmap = BitmapFactory.decodeResource(getResources(), album.getCover());
        Palette p = Palette.from(coverBitmap).generate();
        int color = p.getMutedSwatch().getRgb();

        // Set App Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                finish();
            }
        });

        setTitle(album.getTitle());

        ImageView coverView = (ImageView) findViewById(R.id.cover_toolbar);
        coverView.setImageResource(album.getCover());

        // Set color
        getWindow().setStatusBarColor(color);
        findViewById(R.id.album_activity_content).setBackgroundColor(color);
        findViewById(R.id.album_activity_layout).setBackgroundColor(color);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
