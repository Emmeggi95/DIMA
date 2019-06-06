package com.dima.emmeggi95.jaycaves.me.entities.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.activities.AlbumActivity;
import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.entities.SearchHistory;
import com.dima.emmeggi95.jaycaves.me.entities.db.Album;

import java.util.List;

/**
 * Adapter to display albums found by search in SearchableActivity
 */
public class SearchAlbumsAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Album> albums;
    private SharedPreferences preferences;

    public SearchAlbumsAdapter(Context context, List<Album> albums) {
        this.context = context;
        this.albums = albums;
        preferences = context.getSharedPreferences(context.getString(R.string.search_file_key), Context.MODE_PRIVATE);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView artist;
        public ConstraintLayout container;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.album_title_search);
            artist = itemView.findViewById(R.id.album_artist_search);
            container = itemView.findViewById(R.id.result_container);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_album_list_row, viewGroup, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        final Album album = albums.get(i);
        ItemViewHolder h = (ItemViewHolder) holder;

        h.title.setText(album.getTitle());
        h.artist.setText(album.getArtist());
        h.container.setOnClickListener(new TextView.OnClickListener(){
            @Override
            public void onClick(View v) {
                SearchHistory.putOnTop(context, album.getTitle() + " (" + album.getArtist() + ")");
                Intent intent = new Intent(context, AlbumActivity.class);
                intent.putExtra("album", album);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public void setItems(List<Album> newAlbums) {
        this.albums = newAlbums;
    }
}

