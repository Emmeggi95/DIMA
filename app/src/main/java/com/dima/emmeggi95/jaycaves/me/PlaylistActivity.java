package com.dima.emmeggi95.jaycaves.me;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v13.view.DragStartHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;

import com.dima.emmeggi95.jaycaves.me.entities.Playlist;
import com.dima.emmeggi95.jaycaves.me.entities.PlaylistAlbumsAdapter;
import com.dima.emmeggi95.jaycaves.me.layout.ItemTouchHelperAdapter;
import com.dima.emmeggi95.jaycaves.me.layout.OnStartDragListener;
import com.dima.emmeggi95.jaycaves.me.layout.PlaylistItemTouchHelperCallback;
import com.dima.emmeggi95.jaycaves.me.view_models.PlaylistsViewModel;

public class PlaylistActivity extends AppCompatActivity implements OnStartDragListener {

    Playlist playlist;
    int position;
    PlaylistsViewModel viewModel;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    ItemTouchHelper itemTouchHelper;

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

        playlist = (Playlist) getIntent().getSerializableExtra("playlist");
        position = getIntent().getIntExtra("position", 0);
        viewModel = ViewModelProviders.of(this).get(PlaylistsViewModel.class);

        setTitle(playlist.getName());

        recyclerView= findViewById(R.id.playlist_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        adapter = new PlaylistAlbumsAdapter(this, playlist.getAlbums(), this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new PlaylistItemTouchHelperCallback((ItemTouchHelperAdapter) adapter);
        itemTouchHelper = new ItemTouchHelper(callback);

        final Button edit = findViewById(R.id.edit_playlist);
        final Button submit = findViewById(R.id.submit_playlist);

        submit.setEnabled(false);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit.setEnabled(true);
                v.setEnabled(false);
                itemTouchHelper.attachToRecyclerView(recyclerView);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setEnabled(true);
                v.setEnabled(false);
                itemTouchHelper.attachToRecyclerView(null);
            }
        });


        // TODO
        // Aggiungere toolbar, risolvere il pulsante indietro, verificare se in AlbumActivity serve il finish
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void move(int x, int y) {
        viewModel.moveAlbum(position, x, y);
    }

    @Override
    public void remove(int x) {
        viewModel.removeAlbum(position, x);
    }
}
