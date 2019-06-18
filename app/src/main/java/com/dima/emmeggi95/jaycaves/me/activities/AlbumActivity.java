package com.dima.emmeggi95.jaycaves.me.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.entities.db.Album;
import com.dima.emmeggi95.jaycaves.me.entities.db.Artist;
import com.dima.emmeggi95.jaycaves.me.entities.CoverCache;
import com.dima.emmeggi95.jaycaves.me.entities.NetworkChangeReceiver;
import com.dima.emmeggi95.jaycaves.me.entities.db.Review;
import com.dima.emmeggi95.jaycaves.me.entities.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.widget.Toast;

import static android.view.View.GONE;

/**
 * Activity to display info related to a certain album fetched from the system
 */
public class AlbumActivity extends AppCompatActivity {

    // Layout elements
    Album album;
    ImageView coverView;
    CardView coverCard;
    ProgressBar loading;
    FloatingActionButton fab;
    boolean exiting = false;
    boolean isTransitionExecuted = false;
    BottomNavigationView bottomNavigationView;
    BottomNavigationView bottomNavigationView2;

    // Temp variables
    //List<Playlist> playlists;
    int likes;
    Review featuredReview;
    boolean reviewLiked;
    String id;


    // Db references
    DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");
    DatabaseReference artistsRef = FirebaseDatabase.getInstance().getReference("artists");

    private NetworkChangeReceiver networkChangeReceiver = null;
    private IntentFilter intentFilter;

    private boolean reviewWritten;
    private Review myReview;

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

        // Set floating button
        fab = findViewById(R.id.album_floating_button);
        final Context thisActivity = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String[] playlistNames = new String[User.playlists.size()];
                for (int i = 0; i < User.playlists.size(); i++) {
                    playlistNames[i] = User.playlists.get(i).getName();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                builder.setTitle(getResources().getString(R.string.choose_playlist));
                builder.setItems(playlistNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                            if(User.playlists.get(which).addEntry(album)){
                                 User.updatePlaylist(User.playlists.get(which), album, "ADD");
                                 Snackbar.make(view, album.getTitle() + " " + getResources().getString(R.string.added_to) + " " + User.playlists.get(which).getName(), Snackbar.LENGTH_LONG)
                                             .setAction("Action", null).show();
                            }
                            else{
                                 Snackbar.make(view, album.getTitle() + " is already in "+ User.playlists.get(which).getName(), Snackbar.LENGTH_LONG)
                                         .setAction("Action", null).show();
                            }
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
        setTitle("");

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
        int color = p.getMutedColor(getResources().getColor(R.color.colorSecondaryLight));

        // Set toolbar and fab color
        fab.setBackgroundTintList(ColorStateList.valueOf(color));
        toolbarLayout.setContentScrimColor(color);
        toolbarLayout.setStatusBarScrimColor(color);

        // Set navigation icon color
        if(isTopLeftDark(bitmap)){
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorTextOnDarkBackground), PorterDuff.Mode.SRC_ATOP);
        } else {
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorTextOnLightBackground), PorterDuff.Mode.SRC_ATOP);
        }

        // Set title
        TextView albumTitleText = findViewById(R.id.album_title_text);
        albumTitleText.setText(album.getTitle());

        // Set artist
        TextView artistText = (TextView) findViewById(R.id.artist_text);
        artistText.setText(album.getArtist());

        // Set year
        TextView yearText = (TextView) findViewById(R.id.year_text);
        yearText.setText(album.getDate());

        // Init featured review section
        //hideFeaturedReview();

        // Bottom navigation
        bottomNavigationView = findViewById(R.id.album_bottom_nav);
        bottomNavigationView2 = findViewById(R.id.album_bottom_nav_2);
        reviewWritten = false;

        // Correct the shared element transition setting the final radius of the card containing the album cover to zero
        coverCard = findViewById(R.id.cover_card);
        getWindow().getSharedElementEnterTransition()
                .addListener(new Transition.TransitionListener() {
                    @Override
                    public void onTransitionEnd(Transition transition) {

                    }

                    @Override
                    public void onTransitionCancel(Transition transition) {

                    }

                    @Override
                    public void onTransitionPause(Transition transition) {

                    }

                    @Override
                    public void onTransitionResume(Transition transition) {

                    }

                    @Override
                    public void onTransitionStart(Transition transition) {
                        isTransitionExecuted = true;
                        ObjectAnimator animator;
                        if(exiting){
                            animator = ObjectAnimator.ofFloat(coverCard, "radius", getIntent().getFloatExtra("return_radius", getResources().getDimension(R.dimen.card_radius)));
                        } else {
                            animator = ObjectAnimator
                                    .ofFloat(coverCard, "radius", 0);
                        }
                        animator.setDuration(250);
                        animator.start();
                    }
                });
        if(!getIntent().hasExtra("return_radius")){
            coverCard.setRadius(0);
        }
    }

    @Override
    public void onBackPressed() {
        exiting = true;
        super.onBackPressed();
        hideFab();
    }

    public void showFeaturedReview() {

        // Hide progress bar and show review
        ProgressBar progressBar = findViewById(R.id.progress_bar_review);
        progressBar.setVisibility(GONE);
        View divider = findViewById(R.id.divider1);
        divider.setVisibility(View.VISIBLE);
        ConstraintLayout featuredReviewLayout = findViewById(R.id.featured_review_content);
        featuredReviewLayout.setVisibility(View.VISIBLE);
        TextView reviewNotFound = (TextView) findViewById(R.id.not_found_text);
        reviewNotFound.setVisibility(GONE);

        // Bind elements
        TextView reviewHeader = (TextView) findViewById(R.id.review_title);
        TextView reviewAuthor = (TextView) findViewById(R.id.review_author);
        TextView reviewDate = (TextView) findViewById(R.id.review_date);
        TextView reviewBody = (TextView) findViewById(R.id.review_body);
        //TextView reviewRating = (TextView) findViewById(R.id.review_rating_text);
        final TextView reviewLikes = findViewById(R.id.likes_number);
        //Button edit = findViewById(R.id.review_edit_button);
        List<Review> reviews = album.getReviews();
        Collections.sort(reviews, Review.likesComparator); // featured review is the one with most likes
        if(getIntent().hasExtra("review")){
            System.out.println("FOUND REVIEW IN INTENT");
            featuredReview = (Review) getIntent().getExtras().get("review");
        } else if (album.getReviews().size()>0){
            System.out.println("NO REVIEW IN INTENT, FETCHING FROM ALBUM");
            featuredReview = album.getReviews().get(0);
        }

        // Set stars
        List<ImageView> stars = new ArrayList<ImageView>();
        stars.add((ImageView) featuredReviewLayout.findViewById(R.id.star_1));
        stars.add((ImageView) featuredReviewLayout.findViewById(R.id.star_2));
        stars.add((ImageView) featuredReviewLayout.findViewById(R.id.star_3));
        stars.add((ImageView) featuredReviewLayout.findViewById(R.id.star_4));
        stars.add((ImageView) featuredReviewLayout.findViewById(R.id.star));
        if (album.getScore() == 0.0) {
            stars.get(1).setVisibility(GONE);
            stars.get(2).setVisibility(GONE);
            stars.get(3).setVisibility(GONE);
            stars.get(4).setVisibility(GONE);
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
        reviewAuthor.setOnClickListener(new TextView.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("username", featuredReview.getAuthor());
                intent.putExtra("email", featuredReview.getUserEmail());
                startActivity(intent);
            }
        });
        reviewDate.setText(featuredReview.getShortDate());

        reviewBody.setText(featuredReview.getBody());

        //reviewRating.setText(String.format("%.2f", featuredReview.getRating()));
        String likesText;
        if(featuredReview.getLikes()==1){
            likesText = getResources().getString(R.string.like);
        } else {
            likesText = getResources().getString(R.string.likes);
        }
        reviewLikes.setText(String.valueOf(featuredReview.getLikes()) + ' ' + likesText);

        // Like button
        final ImageView likeSymbol = findViewById(R.id.heart);
        likeSymbol.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!reviewLiked){
                    User.addLike(featuredReview);
                    likeSymbol.setImageResource(R.drawable.ic_favorite_black_24dp);
                    reviewLiked = true;
                    featuredReview.setLikes(featuredReview.getLikes()+1);
                    String likesText;
                    if(featuredReview.getLikes()==1){
                        likesText = getResources().getString(R.string.like);
                    } else {
                        likesText = getResources().getString(R.string.likes);
                    }
                    reviewLikes.setText(String.valueOf(featuredReview.getLikes()) + ' ' + likesText);
                }
                Animation bump = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bump);
                likeSymbol.startAnimation(bump);
            }
        });

        reviewLiked = false;
        for (String like : User.likes) {
            if (like.equals(featuredReview.getId())) {
                reviewLiked = true;
                likeSymbol.setImageResource(R.drawable.ic_favorite_black_24dp);
            }
        }

    }

    public void hideFeaturedReview() {
        // Hide progress bar and show message
        ProgressBar progressBar = findViewById(R.id.progress_bar_review);
        progressBar.setVisibility(GONE);
        View divider = findViewById(R.id.divider1);
        divider.setVisibility(View.VISIBLE);
        ConstraintLayout featuredReviewLayout = (ConstraintLayout) findViewById(R.id.featured_review_content);
        featuredReviewLayout.setVisibility(GONE);
        TextView reviewNotFound = (TextView) findViewById(R.id.not_found_text);
        reviewNotFound.setVisibility(View.VISIBLE);
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
                album.getReviews().clear();
                for (DataSnapshot d : data) {
                    album.getReviews().add(d.getValue(Review.class));
                }
                if (album.getReviews().size() > 0 || getIntent().hasExtra("review")) {
                    showFeaturedReview();
                } else {
                    hideFeaturedReview();
                }
                if(album.getReviews().size() > 1){
                    bottomNavigationView2.getMenu().getItem(0).setCheckable(true);
                } else {
                    bottomNavigationView2.getMenu().getItem(0).setCheckable(false);
                }
                bottomNavigationView2.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.read_reviews:
                                if(!bottomNavigationView2.getMenu().getItem(0).isCheckable()){
                                    Toast toast2 = Toast.makeText(getApplicationContext(), getResources().getString(R.string.message_no_review_available), Toast.LENGTH_LONG);
                                    toast2.show();
                                } else {
                                    Intent intent2 = new Intent(getApplicationContext(), ReviewsActivity.class);
                                    intent2.putExtra("album", album);
                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AlbumActivity.this, coverView, "album_cover");
                                    startActivity(intent2, options.toBundle());
                                }
                                return true;
                        }
                        return false;
                    }
                });
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

        // Set rating
        TextView ratingText = findViewById(R.id.text_rating);
        ratingText.setText(String.format("%.2f", album.getScore()));

        int i = 0;
        while(!reviewWritten && i<User.reviews.size()){
            if (User.reviews.get(i).getTitle().equals(id)){
                reviewWritten = true;
                bottomNavigationView.getMenu().getItem(0).setIcon(R.drawable.ic_baseline_create_24px);
                bottomNavigationView.getMenu().getItem(0).setTitle(R.string.edit_review);
                myReview = User.reviews.get(i);
            }
            i++;
        }


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.write_review:
                        Intent intent = new Intent(getApplicationContext(), NewReviewActivity.class);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AlbumActivity.this, coverView, "album_cover");
                        intent.putExtra("album", album);
                        if(reviewWritten){
                            intent.putExtra("review", myReview);
                        }
                        startActivity(intent, options.toBundle());
                        return true;
                }
                return false;
            }
        });


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
