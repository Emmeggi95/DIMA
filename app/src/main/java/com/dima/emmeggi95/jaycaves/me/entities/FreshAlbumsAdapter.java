package com.dima.emmeggi95.jaycaves.me.entities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.Album;
import com.dima.emmeggi95.jaycaves.me.R;

import java.util.List;

public class FreshAlbumsAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Album> albumList;

    public FreshAlbumsAdapter(Context context, List<Album> albumList) {
        this.context = context;
        this.albumList = albumList;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title, artist, rating;
        ImageView cover;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.fresh_title_text);
            artist = itemView.findViewById(R.id.fresh_artist_text);
            rating = itemView.findViewById(R.id.fresh_rating_text);
            cover = itemView.findViewById(R.id.fresh_cover);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fresh_album_card, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        Album album = albumList.get(i);

        ((ItemViewHolder) holder).title.setText(album.getTitle());
        ((ItemViewHolder) holder).artist.setText(album.getArtist());
        ((ItemViewHolder) holder).rating.setText(String.format("%.2f", album.getScore()));
        // Set cover...

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
