package com.dima.emmeggi95.jaycaves.me;

import android.app.SearchManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.entities.SearchAlbumsAdapter;
import com.dima.emmeggi95.jaycaves.me.entities.SearchArtistsAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchableActivity extends AppCompatActivity {

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

    // Prova
    private List<Artist> artistList;
    private List<Album> albumList;

    //
    private NetworkChangeReceiver networkChangeReceiver = null;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        //getWindow().setEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.search_transition));

        Toolbar toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchView = findViewById(R.id.search_view);

        artistRecyclerView = findViewById(R.id.search_artist_recycler_view);
        albumRecyclerView = findViewById(R.id.search_album_recycler_view);
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

        //Prova
        albumList = new ArrayList<>();
        albumList.add(new Album("Album #1", "1995", "Artist ABC", "Rock", ""));
        albumList.add(new Album("Album #2", "1995", "Artist ABC", "Rock", ""));
        albumList.add(new Album("Album #3", "1995", "Artist ABC", "Rock", ""));
        //
        artistList = new ArrayList<>();
        artistList.add(new Artist("Tizio", "1950", "...", "Rock", ""));
        artistList.add(new Artist("Caio", "1950", "...", "Rock", ""));
        artistList.add(new Artist("Sempronio", "1950", "...", "Rock", ""));
        //
    }

    private void performSearch(String query){
        if(!query.equals("")) {
            // Database connection...

            // Prova
            artistResults.clear();
            for (Artist a : artistList) {
                if (a.getName().toLowerCase().contains(query.toLowerCase())) {
                    artistResults.add(a);
                }
            }

            albumResults.clear();
            for (Album a : albumList) {
                if (a.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    albumResults.add(a);
                }
            }

            if (artistResults.size() > 0) {
                artistHeader.setVisibility(View.VISIBLE);
            } else {
                artistHeader.setVisibility(View.GONE);
            }

            if (albumResults.size()>0) {
                albumHeader.setVisibility(View.VISIBLE);
            } else {
                albumHeader.setVisibility(View.GONE);
            }

        } else {
            artistHeader.setVisibility(View.GONE);
            artistResults.clear();
            albumHeader.setVisibility(View.GONE);
            albumResults.clear();
        }

        updateList();
    }

    private void updateList(){
        artistAdapter.setItems(artistResults);
        artistAdapter.notifyDataSetChanged();
        albumAdapter.setItems(albumResults);
        albumAdapter.notifyDataSetChanged();
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
}
