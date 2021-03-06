package com.dima.emmeggi95.jaycaves.me.activities;

import android.animation.Animator;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.entities.db.Album;
import com.dima.emmeggi95.jaycaves.me.entities.db.Artist;
import com.dima.emmeggi95.jaycaves.me.entities.NetworkChangeReceiver;
import com.dima.emmeggi95.jaycaves.me.entities.adapters.SearchAlbumsAdapter;
import com.dima.emmeggi95.jaycaves.me.entities.adapters.SearchArtistsAdapter;
import com.dima.emmeggi95.jaycaves.me.entities.SearchHistory;
import com.dima.emmeggi95.jaycaves.me.entities.adapters.SearchHistoryAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows search of contents
 */
public class SearchableActivity extends AppCompatActivity {

    private View rootLayout;
    private int cx;
    private int cy;

    private SearchView searchView;
    private RecyclerView artistRecyclerView;
    private RecyclerView albumRecyclerView;

    private SearchArtistsAdapter artistAdapter;
    private SearchAlbumsAdapter albumAdapter;

    private RecyclerView.LayoutManager artistLayoutManager;
    private RecyclerView.LayoutManager albumLayoutManager;

    private List<Artist> artistResults;
    private List<Album> albumResults;

    private TextView artistHeader;
    private TextView albumHeader;
    private TextView noResult;
    private TextView newContent;
    private View divider;
    private ProgressBar progressBar;

    private List<String> history;
    private RecyclerView historyRecyclerView;
    private SearchHistoryAdapter historyAdapter;
    private RecyclerView.LayoutManager historyLayoutManager;
    private LinearLayout clearHistory;
    private boolean emptySearchField;

    private DatabaseReference albumReference= FirebaseDatabase.getInstance().getReference("albums");
    private DatabaseReference artistReference= FirebaseDatabase.getInstance().getReference("artists");
    private NetworkChangeReceiver networkChangeReceiver = null;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
        rootLayout = findViewById(R.id.root_layout);
        rootLayout.setVisibility(View.INVISIBLE);

        if(getIntent().hasExtra(getString(R.string.x_axis)) && getIntent().hasExtra(getString(R.string.y_axis))) {
            cx = getIntent().getIntExtra(getString(R.string.x_axis), 0);
            cy = getIntent().getIntExtra(getString(R.string.y_axis), 0);
        } else {
            cx = rootLayout.getWidth()/2;
            cy = rootLayout.getHeight()/2;
        }

        ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

                    // create the animator for this view (the start radius is zero)
                    Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
                    circularReveal.setDuration(300);

                    // make the view visible and start the animation
                    rootLayout.setVisibility(View.VISIBLE);
                    circularReveal.start();

                    rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }

        Toolbar toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchView = findViewById(R.id.search_view);

        artistRecyclerView = findViewById(R.id.search_artist_recycler_view);
        artistRecyclerView.setHasFixedSize(true);
        albumRecyclerView = findViewById(R.id.search_album_recycler_view);
        albumRecyclerView.setHasFixedSize(true);
        artistLayoutManager = new LinearLayoutManager(this);
        albumLayoutManager = new LinearLayoutManager(this);
        artistRecyclerView.setLayoutManager(artistLayoutManager);
        albumRecyclerView.setLayoutManager(albumLayoutManager);

        artistResults = new ArrayList<>();
        albumResults = new ArrayList<>();
        albumAdapter = new SearchAlbumsAdapter(this, albumResults);
        albumRecyclerView.setAdapter(albumAdapter);
        artistAdapter = new SearchArtistsAdapter(this, artistResults);
        artistRecyclerView.setAdapter(artistAdapter);

        // Search history
        history = SearchHistory.getHistory(getApplicationContext());
        clearHistory = findViewById(R.id.clear_history);

        historyRecyclerView = findViewById(R.id.history_recycler_view);
        historyAdapter = new SearchHistoryAdapter(this, this, history);
        historyLayoutManager = new LinearLayoutManager(this);
        historyRecyclerView.setLayoutManager(historyLayoutManager);
        historyRecyclerView.setAdapter(historyAdapter);
        if(history.size()>0){
            clearHistory.setVisibility(View.VISIBLE);
        }

        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchHistory.clearHistory(getApplicationContext());
                history = SearchHistory.getHistory(getApplicationContext());
                historyAdapter.setItems(history);
                historyAdapter.notifyDataSetChanged();
                clearHistory.setVisibility(View.GONE);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;

            }
        });


        artistHeader = findViewById(R.id.artist_search_header);
        albumHeader = findViewById(R.id.album_search_header);
        noResult = findViewById(R.id.no_result_text);
        newContent = findViewById(R.id.new_content);
        divider = findViewById(R.id.divider_album_artist);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.colorAccentMuted), android.graphics.PorterDuff.Mode.SRC_IN);

        newContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddContentActivity.class);
                startActivity(intent);
            }
        });

        emptySearchField = true;

    }

    private synchronized void performSearch(final String query){
        if(!query.equals("")) {
            emptySearchField = false;
            historyAdapter.setItems(new ArrayList<String>());
            historyAdapter.notifyDataSetChanged();
            clearHistory.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            final String parsedQuery = query.substring(0, 1).toUpperCase() + query.substring(1);

            artistReference.orderByChild("name").startAt(parsedQuery).limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!emptySearchField) {
                        progressBar.setVisibility(View.GONE);
                        Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                        artistResults.clear();
                        for (DataSnapshot d : data) {
                            if (d.getKey().contains(parsedQuery))
                                artistResults.add(d.getValue(Artist.class));
                        }
                        if (artistResults.size() > 0) {
                            artistHeader.setVisibility(View.VISIBLE);
                            checkIfResultsExist(query);
                            if(albumResults.size() > 0){
                                divider.setVisibility(View.VISIBLE);
                            }

                        } else {
                            artistHeader.setVisibility(View.GONE);
                            checkIfResultsExist(query);
                        }
                        artistAdapter.setItems(artistResults);
                        artistAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            albumReference.orderByChild("title").startAt(parsedQuery).limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!emptySearchField) {
                        progressBar.setVisibility(View.GONE);
                        Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                        albumResults.clear();
                        for (DataSnapshot d : data) {
                            Album album = d.getValue(Album.class);
                            if (album.getTitle().contains(parsedQuery))
                                albumResults.add(album);
                        }
                        if (albumResults.size() > 0) {
                            albumHeader.setVisibility(View.VISIBLE);
                            checkIfResultsExist(query);
                            if(artistResults.size() > 0){
                                divider.setVisibility(View.VISIBLE);
                            }
                        } else {
                            albumHeader.setVisibility(View.GONE);
                            checkIfResultsExist(query);

                        }
                        albumAdapter.setItems(albumResults);
                        albumAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } else {
            // Do the following if the search field is empty
            checkIfResultsExist(query);
            progressBar.setVisibility(View.GONE);

        }

        // NOTHING VISIBLE AS DEFAULT OPTION
        artistHeader.setVisibility(View.GONE);
        artistResults.clear();
        albumHeader.setVisibility(View.GONE);
        albumResults.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

    @Override
    public void onBackPressed() {
        float startRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator circularHide = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, startRadius, 0);
        circularHide.setDuration(300);

        circularHide.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                rootLayout.setVisibility(View.INVISIBLE);
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        // start the animation
        circularHide.start();

    }

    public void clickOnHistoryItem(String item){
        String query;
        if(item.contains("(")){
            String[] parts = item.split("[(]");
            query = parts[0].trim();
        } else {
            query = item;
        }
        searchView.setQuery(query, true);
    }

    private void checkIfResultsExist(String query){
        divider.setVisibility(View.GONE);
        if(albumHeader.getVisibility()==View.GONE && artistHeader.getVisibility()==View.GONE && !query.equals("")){
            noResult.setVisibility(View.VISIBLE);
            newContent.setVisibility(View.VISIBLE);
        } else {
            noResult.setVisibility(View.GONE);
            newContent.setVisibility(View.GONE);
            if(query.equals("")){
                history = SearchHistory.getHistory(getApplicationContext());
                historyAdapter.setItems(history);
                historyAdapter.notifyDataSetChanged();
                if(history.size()>0){
                    clearHistory.setVisibility(View.VISIBLE);
                }
                clearSearch();
            }
        }
    }

    private void clearSearch(){
        emptySearchField = true;
        artistAdapter.setItems(new ArrayList<Artist>());
        artistAdapter.notifyDataSetChanged();
        artistHeader.setVisibility(View.GONE);
        albumAdapter.setItems(new ArrayList<Album>());
        albumAdapter.notifyDataSetChanged();
        albumHeader.setVisibility(View.GONE);
    }
}
