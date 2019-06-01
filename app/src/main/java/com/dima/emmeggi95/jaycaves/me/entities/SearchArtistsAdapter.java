package com.dima.emmeggi95.jaycaves.me.entities;

import android.content.ClipData;
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

import com.dima.emmeggi95.jaycaves.me.Artist;
import com.dima.emmeggi95.jaycaves.me.ArtistActivity;
import com.dima.emmeggi95.jaycaves.me.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * Adapter to display artist found by search in SearchableActivity
 */
public class SearchArtistsAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Artist> artists;
    private SharedPreferences preferences;

    public SearchArtistsAdapter(Context context, List<Artist> artists) {
        this.context = context;
        this.artists = artists;
        preferences = context.getSharedPreferences(context.getString(R.string.search_history_key), Context.MODE_PRIVATE);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView artist;
        ConstraintLayout container;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            artist = itemView.findViewById(R.id.artist_name_search);
            container = itemView.findViewById(R.id.result_container);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_artist_list_row, viewGroup, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        final Artist artist = artists.get(i);
        ItemViewHolder h = (ItemViewHolder) holder; 

        h.artist.setText(artist.getName());
        h.container.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchHistory.putOnTop(context, artist.getName());
                Intent intent = new Intent(context, ArtistActivity.class);
                intent.putExtra("artist", artist);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public void setItems(List<Artist> newArtists){
        this.artists = newArtists;
    }
}
