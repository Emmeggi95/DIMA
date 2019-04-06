package com.dima.emmeggi95.jaycaves.me.entities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.Album;
import com.dima.emmeggi95.jaycaves.me.AlbumActivity;
import com.dima.emmeggi95.jaycaves.me.CoverCache;
import com.dima.emmeggi95.jaycaves.me.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ChartAlbumsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Album> albums;

    public ChartAlbumsAdapter(Context context, List<Album> albums) {
        this.context = context;
        this.albums = albums;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        CardView card;
        TextView rank, title, artist, score, votes;
        ImageView cover;
        ProgressBar loading;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            card = (CardView) itemView.findViewById(R.id.chart_album_card);
            rank = (TextView) itemView.findViewById(R.id.rank_text);
            title = (TextView) itemView.findViewById(R.id.chart_title_text);
            artist = (TextView) itemView.findViewById(R.id.chart_artist_text);
            score = (TextView) itemView.findViewById(R.id.chart_rating_text);
            votes = (TextView) itemView.findViewById(R.id.votes_number_text);
            cover = (ImageView) itemView.findViewById(R.id.chart_cover);
            loading= itemView.findViewById(R.id.loading_chart_album_card);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chart_album_card, viewGroup, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        final Album album = albums.get(i);

        // Set album info
        ((ItemViewHolder) holder).rank.setText(String.valueOf(i+1));
        ((ItemViewHolder) holder).title.setText(album.getTitle());
        ((ItemViewHolder) holder).artist.setText(album.getArtist());
        ((ItemViewHolder) holder).score.setText(String.format("%.2f", album.getScore()));
        ((ItemViewHolder) holder).votes.setText("n/a");// Da implementare!

        // Set cover
        CoverCache.retrieveCover(album.getCover(), ((ChartAlbumsAdapter.ItemViewHolder)holder).cover, ((ChartAlbumsAdapter.ItemViewHolder)holder).loading,
                context.getApplicationContext().getDir(CoverCache.INTERNAL_DIRECTORY_ALBUM,MODE_PRIVATE));

        // Set on click to album activity
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
