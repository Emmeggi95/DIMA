package com.dima.emmeggi95.jaycaves.me;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.entities.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class AlbumActivity extends AppCompatActivity {

    Album album;
    ImageView coverView;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        // Get album from intent extra
        album = (Album) getIntent().getSerializableExtra("album");

        // Set App Bar
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.album_activity_toolbar_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.album_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                finish();
            }
        });
        setTitle(album.getTitle());


        // Set Cover Image
        loading= findViewById(R.id.loading_album);
        coverView = findViewById(R.id.cover_toolbar);
        coverView.setImageResource(R.drawable.default_cover);

        // Retrieve cover from cache
        CoverCache.retrieveCover(album.getCover(),coverView, loading,
                getApplicationContext().getDir(CoverCache.INTERNAL_DIRECTORY_ALBUM,MODE_PRIVATE));

        // Get color from artist cover and set it to the UI elements
        BitmapDrawable drawable = (BitmapDrawable) coverView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Palette p = Palette.from(bitmap).generate();
        int color;
        if(p.getMutedSwatch()!=null) {
            color = p.getMutedSwatch().getRgb();
        } else {
            color = R.color.colorAccent;
        }


        // Set floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.album_floating_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        // Set color
        fab.setBackgroundTintList(ColorStateList.valueOf(color));
        toolbarLayout.setContentScrimColor(color);
        toolbarLayout.setStatusBarScrimColor(color);

        // Set artist
        TextView artistText = (TextView) findViewById(R.id.artist_text);
        artistText.setText(album.getArtist());

        // Set year
        TextView yearText = (TextView) findViewById(R.id.year_text);
        yearText.setText(album.getDate());

        // Set stars
        List<ImageView> stars = new ArrayList<ImageView>();
        stars.add((ImageView) findViewById(R.id.star_1));
        stars.add((ImageView) findViewById(R.id.star_2));
        stars.add((ImageView) findViewById(R.id.star_3));
        stars.add((ImageView) findViewById(R.id.star_4));
        stars.add((ImageView) findViewById(R.id.star_5));
        int integerScore = (int) album.getScore();
        int i;
        for(i=0; i<integerScore; i++){
            stars.get(i).setImageResource(R.drawable.ic_star_24dp);
        }
        float decimalPart = (float) album.getScore() - integerScore;
        if(decimalPart >= 0.5){
            stars.get(i).setImageResource(R.drawable.ic_star_half_24dp);
        }
        TextView rating = (TextView) findViewById(R.id.text_rating);
        rating.setText(String.format("%.2f", album.getScore()));

        // Set featured review
        if(album.getReviews().size()>0){
            showFeaturedReview();
        } else {
            hideFeaturedReview();
        }
    }

    public void showFeaturedReview(){
        TextView reviewTitle = (TextView) findViewById(R.id.review_title);
        TextView reviewAuthor = (TextView) findViewById(R.id.review_author);
        TextView reviewDate = (TextView) findViewById(R.id.review_date);
        TextView reviewBody = (TextView) findViewById(R.id.review_body);
        TextView reviewRating = (TextView) findViewById(R.id.review_rating_text);
        TextView reviewLikes = findViewById(R.id.likes_number);
        Review featuredReview = album.getReviews().get(0);
        reviewTitle.setText(featuredReview.getTitle());
        reviewAuthor.setText(featuredReview.getAuthor());
        reviewDate.setText(featuredReview.getDate());
        reviewBody.setText(featuredReview.getBody());
        reviewRating.setText(String.format("%.2f", featuredReview.getRating()));
        reviewLikes.setText(String.valueOf(featuredReview.getLikes()));
        LinearLayout featuredReviewLayout = (LinearLayout) findViewById(R.id.featured_review);
        featuredReviewLayout.setVisibility(View.VISIBLE);
        TextView reviewNotFound = (TextView) findViewById(R.id.not_found_text);
        reviewNotFound.setVisibility(GONE);
        Button seeAllReviewsButton = (Button) findViewById(R.id.all_reviews_button);
        if(album.getReviews().size()>0){
            seeAllReviewsButton.setEnabled(true);
        } else {
            seeAllReviewsButton.setEnabled(false);
        }

    }

    public void hideFeaturedReview(){
        LinearLayout featuredReviewLayout = (LinearLayout) findViewById(R.id.featured_review);
        featuredReviewLayout.setVisibility(GONE);
        TextView reviewNotFound = (TextView) findViewById(R.id.not_found_text);
        reviewNotFound.setVisibility(View.VISIBLE);
        Button seeAllReviewsButton = (Button) findViewById(R.id.all_reviews_button);
        seeAllReviewsButton.setEnabled(false);
    }

    public void startReviewsActivity(View view){
        Intent intent = new Intent(this, ReviewsActivity.class);
        intent.putExtra("album", album);
        startActivity(intent);
    }

    public void startArtistActivity(View view){
        final Intent intent = new Intent(this, ArtistActivity.class);
        // Retrieve information about the artist...
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("artists");
        reference.orderByKey().equalTo(album.getArtist()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Artist artist = new Artist();
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for(DataSnapshot d : data){
                    artist= d.getValue(Artist.class);
                }
                intent.putExtra("artist", artist);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
