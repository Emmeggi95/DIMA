package com.dima.emmeggi95.jaycaves.me;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.IntentFilter;
import android.support.v13.view.DragStartHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.entities.Playlist;
import com.dima.emmeggi95.jaycaves.me.entities.PlaylistAlbumsAdapter;
import com.dima.emmeggi95.jaycaves.me.layout.ItemTouchHelperAdapter;
import com.dima.emmeggi95.jaycaves.me.layout.OnStartDragListener;
import com.dima.emmeggi95.jaycaves.me.layout.PlaylistItemTouchHelperCallback;
import com.dima.emmeggi95.jaycaves.me.view_models.PlaylistsViewModel;

public class PlaylistActivity extends AppCompatActivity implements OnStartDragListener {

    Playlist playlist;
    int position;


    RecyclerView recyclerView;
    PlaylistAlbumsAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    ItemTouchHelper itemTouchHelper;

    Menu menu;

    private NetworkChangeReceiver networkChangeReceiver = null;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        Toolbar toolbar = findViewById(R.id.playlist_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //playlist = (Playlist) getIntent().getSerializableExtra("playlist");
        position = getIntent().getIntExtra("position", 0);
        playlist = User.playlists.get(position);
       // viewModel = ViewModelProviders.of(this).get(PlaylistsViewModel.class);

        setTitle(playlist.getName());

        recyclerView= findViewById(R.id.playlist_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        adapter = new PlaylistAlbumsAdapter(this, playlist.getAlbums(), this);

        recyclerView.setLayoutManager(layoutManager);

        // set animation for the recyclerview
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_list);
        recyclerView.setLayoutAnimation(animation);

        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new PlaylistItemTouchHelperCallback((ItemTouchHelperAdapter) adapter);
        itemTouchHelper = new ItemTouchHelper(callback);

        // TODO
        // Aggiungere toolbar, risolvere il pulsante indietro, verificare se in AlbumActivity serve il finish
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.playlist_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        MenuItem edit = menu.findItem(R.id.action_edit);
        MenuItem submit = menu.findItem(R.id.action_submit);
        final TextView instructions = findViewById(R.id.editing_instructions);
        ImageView sortSymbol = findViewById(R.id.sort_symbol);
        if(id == R.id.action_edit){
            edit.setVisible(false);
            submit.setVisible(true);

            //recyclerView.animate().translationY(instructions.getHeight());
            //instructions.setVisibility(View.VISIBLE);
            itemTouchHelper.attachToRecyclerView(recyclerView);
            adapter.showSortSymbol();
            return true;
        }
        if(id == R.id.action_submit){
            edit.setVisible(true);
            submit.setVisible(false);
            instructions.setVisibility(View.GONE);
            itemTouchHelper.attachToRecyclerView(null);
            adapter.hideSortSymbol();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void move(int x, int y) {
        System.out.println("AFTER MOVE from: "+ x + " to: "+ y + "; " +playlist.toString());
        User.reorderPlaylist(playlist);
    }

    @Override
    public void remove(int x) {
        User.updatePlaylist(playlist, playlist.getAlbums().get(x), "REMOVE");
        playlist.removeAlbum(x);
        User.reorderPlaylist(playlist);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //authentication.removeAuthStateListener(authStateListener);
        if(this.networkChangeReceiver!=null) {
            unregisterReceiver(this.networkChangeReceiver);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //authentication.addAuthStateListener(authStateListener);
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
