package com.dima.emmeggi95.jaycaves.me.entities;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.Album;
import com.dima.emmeggi95.jaycaves.me.AlbumActivity;
import com.dima.emmeggi95.jaycaves.me.R;

import java.util.List;

public class ArtistAlbumsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Album> albums;

    public ArtistAlbumsAdapter(Context context, List<Album> albums) {
        this.context = context;
        this.albums = albums;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView title, date, rating;
        ImageView cover;
        CardView card;

        public ItemViewHolder(@NonNull View view) {
            super(view);

            this.title = (TextView) view.findViewById(R.id.artist_album_title);
            this.date = (TextView) view.findViewById(R.id.artist_album_date);
            this.rating = (TextView) view.findViewById(R.id.artist_album_rating_text);
            this.cover = (ImageView) view.findViewById(R.id.artist_album_cover);
            this.card = (CardView) view.findViewById(R.id.artist_album_card_view);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_album_card, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Album album = albums.get(position);

        // Set album info
        ((ItemViewHolder) holder).title.setText(album.getTitle());
        ((ItemViewHolder) holder).date.setText(album.getDate());
        ((ItemViewHolder) holder).rating.setText(String.format("%.2f", album.getScore()));
        // Set cover ((ItemViewHolder) holder).cover...

        // Set card listener to start album activity
        ((ItemViewHolder) holder).card.setOnClickListener(new CardView.OnClickListener() {
            @Override
            public void onClick(View v) {
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
}
