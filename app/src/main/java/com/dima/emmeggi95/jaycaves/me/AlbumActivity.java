package com.dima.emmeggi95.jaycaves.me;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.entities.Playlist;
import com.dima.emmeggi95.jaycaves.me.entities.Review;
import com.dima.emmeggi95.jaycaves.me.view_models.PlaylistsViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.view.View.GONE;

/**
 * Activity to display info related to a certain album fetched from the system
 */
public class AlbumActivity extends AppCompatActivity {

    // Layout elements
    Album album;
    ImageView coverView;
    ProgressBar loading;
    FloatingActionButton fab;
    PlaylistsViewModel playlistsViewModel;

    // Temp variables
    List<Playlist> playlists;
    int likes;
    Review featuredReview;
    boolean reviewLiked;
    String id;
    Button writeReviewButton;


    // Db references
    DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");
    DatabaseReference artistsRef = FirebaseDatabase.getInstance().getReference("artists");

    private NetworkChangeReceiver networkChangeReceiver = null;
    private IntentFilter intentFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_album);

        // Get album from intent extra
        album = (Album) getIntent().getSerializableExtra("album");
        id = album.getTitle() + "@" + album.getArtist();

        // Fetch related reviews
        fetchReviews();

        playlistsViewModel = ViewModelProviders.of(this).get(PlaylistsViewModel.class);
        final Observer<List<Playlist>> observer = new Observer<List<Playlist>>() {
            @Override
            public void onChanged(@Nullable List<Playlist> data) {
                playlists = data;
            }
        };
        playlistsViewModel.getData().observe(this, observer);


        // Set floating button
        fab = findViewById(R.id.album_floating_button);
        final Context thisActivity = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String[] playlistNames = new String[playlists.size()];
                for (int i = 0; i < playlists.size(); i++) {
                    playlistNames[i] = playlists.get(i).getName();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                builder.setTitle(getResources().getString(R.string.choose_playlist));
                builder.setItems(playlistNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(playlistsViewModel.addAlbum(which, album))
                            Snackbar.make(view, album.getTitle() + " " + getResources().getString(R.string.added_to) + " " + playlists.get(which).getName(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        else
                            Snackbar.make(view, album.getTitle() + " is already in "+ playlists.get(which).getName(), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                    }
                });
                builder.show();
            }
        });
        fab.animate().scaleX(1).setDuration(300).setStartDelay(300);
        fab.animate().scaleY(1).setDuration(300).setStartDelay(300);

        // Set App Bar
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.album_activity_toolbar_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.album_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.animate().scaleX(0).setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime)).setListener(null);
                onBackPressed();
            }
        });
        setTitle(album.getTitle());

        // Set Cover Image
        loading = findViewById(R.id.loading_album);
        coverView = findViewById(R.id.cover_toolbar);
        coverView.setImageResource(R.drawable.default_cover);

        // Retrieve cover from cache
        if (album.getCover() != null && album.getCover() != "") {
            CoverCache.retrieveCover(album.getCover(), coverView, loading,
                    getApplicationContext().getDir(CoverCache.INTERNAL_DIRECTORY_ALBUM, MODE_PRIVATE));
        }

        // Get color from artist cover and set it to the UI elements
        BitmapDrawable drawable = (BitmapDrawable) coverView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Palette p = Palette.from(bitmap).generate();
        int color;
        if (p.getMutedSwatch() != null) {
            color = p.getMutedSwatch().getRgb();
        } else {
            color = R.color.colorAccent;
        }

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
        TextView ratingText = findViewById(R.id.text_rating);
        if (album.getScore() == 0.0) {
            stars.get(1).setVisibility(GONE);
            stars.get(2).setVisibility(GONE);
            stars.get(3).setVisibility(GONE);
            stars.get(4).setVisibility(GONE);
            ratingText.setText(getResources().getString(R.string.not_available));
        } else {
            int integerScore = (int) album.getScore();
            int i;
            for (i = 0; i < integerScore; i++) {
                stars.get(i).setImageResource(R.drawable.ic_star_24dp);
            }
            float decimalPart = (float) album.getScore() - integerScore;
            if (decimalPart >= 0.5) {
                stars.get(i).setImageResource(R.drawable.ic_star_half_24dp);
            }
            ratingText.setText(String.format("%.2f", album.getScore()));
        }

        // Init featured review section
        hideFeaturedReview();


        // Write a review button
        writeReviewButton = findViewById(R.id.write_review_button);
        for (Review r : User.getReviews())
            if (r.getTitle().equals(id))
                writeReviewButton.setEnabled(false);
        writeReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewReviewActivity.class);
                intent.putExtra("album", album);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideFab();
    }

    public void showFeaturedReview() {

        // Bind elements
        TextView reviewHeader = (TextView) findViewById(R.id.review_title);
        TextView reviewAuthor = (TextView) findViewById(R.id.review_author);
        TextView reviewDate = (TextView) findViewById(R.id.review_date);
        TextView reviewBody = (TextView) findViewById(R.id.review_body);
        TextView reviewRating = (TextView) findViewById(R.id.review_rating_text);
        final TextView reviewLikes = findViewById(R.id.likes_number);
        Button edit = findViewById(R.id.review_edit_button);
        List<Review> reviews = album.getReviews();
        Collections.sort(reviews, Review.likesComparator); // featured review is the one with most likes
        featuredReview = album.getReviews().get(0);
        if (featuredReview.getAuthor().equals(User.getUsername())) {
            edit.setVisibility(View.VISIBLE);

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), NewReviewActivity.class);
                    intent.putExtra("review", featuredReview);
                    intent.putExtra("album", album);
                    startActivity(intent);
                }
            });
        }



        // Init likes
        likes = featuredReview.getLikes();
        reviewsRef.orderByKey().equalTo(featuredReview.getTitle()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for (DataSnapshot d : data) {
                    likes = d.getValue(Review.class).getLikes();
                    reviewLikes.setText(String.valueOf(featuredReview.getLikes()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // Set contents
        reviewHeader.setText(featuredReview.getHeadline());
        reviewAuthor.setText(featuredReview.getAuthor());
        reviewDate.setText(featuredReview.getShortDate());
        reviewBody.setText(featuredReview.getBody());
        reviewRating.setText(String.format("%.2f", featuredReview.getRating()));
        reviewLikes.setText(String.valueOf(featuredReview.getLikes()));
        LinearLayout featuredReviewLayout = findViewById(R.id.featured_review);
        featuredReviewLayout.setVisibility(View.VISIBLE);
        TextView reviewNotFound = findViewById(R.id.not_found_text);
        reviewNotFound.setVisibility(GONE);
        Button seeAllReviewsButton = findViewById(R.id.all_reviews_button);
        if (album.getReviews().size() > 0) {
            seeAllReviewsButton.setEnabled(true);
        } else {
            seeAllReviewsButton.setEnabled(false);
        }

        // Button "like" and listener
        final Button likeButton = findViewById(R.id.review_like_button);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User.addLike(featuredReview.getId());
                reviewLikes.setText(String.valueOf(featuredReview.getLikes() + 1));
                likeButton.setText("LIKED");
                likeButton.setEnabled(false);


            }
        });

        for (String like : User.getLikes()) {
            if (like.equals(featuredReview.getId())) {
                likeButton.setText("LIKED");
                likeButton.setEnabled(false);
            }
        }

        // Alternative button
        final ImageView likeSymbol = findViewById(R.id.heart);
        likeSymbol.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!reviewLiked){
                    likeSymbol.setImageResource(R.drawable.ic_favorite_black_24dp);
                    reviewLiked = true;
                }
                Animation bump = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bump);
                likeSymbol.startAnimation(bump);
            }
        });

        reviewLiked = false;
        for (String like : User.getLikes()) {
            if (like.equals(featuredReview.getId())) {
                reviewLiked = true;
                likeSymbol.setImageResource(R.drawable.ic_favorite_black_24dp);
            }
        }

    }

    public void hideFeaturedReview() {
        LinearLayout featuredReviewLayout = (LinearLayout) findViewById(R.id.featured_review);
        featuredReviewLayout.setVisibility(GONE);
        TextView reviewNotFound = (TextView) findViewById(R.id.not_found_text);
        reviewNotFound.setVisibility(View.VISIBLE);
        Button seeAllReviewsButton = (Button) findViewById(R.id.all_reviews_button);
        seeAllReviewsButton.setEnabled(false);
    }

    public void startReviewsActivity(View view) {
        Intent intent = new Intent(this, ReviewsActivity.class);
        intent.putExtra("album", album);
        startActivity(intent);
    }

    public void startArtistActivity(View view) {
        final Intent intent = new Intent(this, ArtistActivity.class);
        // Retrieve information about the artist...
        DatabaseReference reference = artistsRef;
        reference.orderByKey().equalTo(album.getArtist()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Artist artist = new Artist();
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for (DataSnapshot d : data) {
                    artist = d.getValue(Artist.class);
                }
                intent.putExtra("artist", artist);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    @SuppressLint("RestrictedApi")
    private void hideFab() {
        fab.clearAnimation();
        fab.setVisibility(GONE);
    }

    private void fetchReviews() {

        reviewsRef.orderByChild("title").equalTo(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for (DataSnapshot d : data) {
                    album.getReviews().add(d.getValue(Review.class));
                }
                if (album.getReviews().size() > 0)
                    showFeaturedReview();
                for (Review r: album.getReviews())
                    if (r.getAuthor().equals(User.getUsername()))
                        writeReviewButton.setEnabled(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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


}
