package com.dima.emmeggi95.jaycaves.me.entities.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
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

import com.dima.emmeggi95.jaycaves.me.activities.AlbumActivity;
import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.entities.CoverCache;
import com.dima.emmeggi95.jaycaves.me.entities.db.Album;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Adapter to display albums in fresh section
 */
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
        public ProgressBar loading;
        CardView card;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.fresh_title_text);
            artist = itemView.findViewById(R.id.fresh_artist_text);
            rating = itemView.findViewById(R.id.fresh_rating_text);
            cover = itemView.findViewById(R.id.fresh_cover);
            loading = itemView.findViewById(R.id.fresh_loading_cover);
            card= itemView.findViewById(R.id.fresh_album_card);
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
        final Album album = albumList.get(i);
        final ItemViewHolder h = (ItemViewHolder) holder;

        h.title.setText(album.getTitle());
        h.artist.setText(album.getArtist());
        h.rating.setText(String.format("%.2f", album.getScore()));

        // Fetch image from storage
        CoverCache.retrieveCover(album.getCover(),((FreshAlbumsAdapter.ItemViewHolder) holder).cover, ((FreshAlbumsAdapter.ItemViewHolder) holder).loading,
                context.getApplicationContext().getDir(CoverCache.INTERNAL_DIRECTORY_ALBUM,MODE_PRIVATE));

        // Set on click to album activity
        h.card.setOnClickListener(new CardView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AlbumActivity.class);
                intent.putExtra("album", album);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, h.cover, "album_cover");
                context.startActivity(intent, options.toBundle());
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
