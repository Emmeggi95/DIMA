package com.dima.emmeggi95.jaycaves.me.entities.adapters;

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
import com.dima.emmeggi95.jaycaves.me.activities.ArtistActivity;
import com.dima.emmeggi95.jaycaves.me.entities.CoverCache;
import com.dima.emmeggi95.jaycaves.me.entities.db.Album;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * Adapter to display albums into a RecyclerView in @ArtistActivity
 * @see ArtistActivity
 */
public class ArtistAlbumsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Album> albums;

    public ArtistAlbumsAdapter(Context context, List<Album> albums) {
        this.context = context;
        this.albums = albums;
    }

    /**
     * Defines single item's structure and contents
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder {

       public TextView title, date, rating;
       public ImageView cover;
       public CardView card;
       public ProgressBar loading;

        public ItemViewHolder(@NonNull View view) {
            super(view);

            this.title = (TextView) view.findViewById(R.id.artist_album_title);
            this.date = (TextView) view.findViewById(R.id.artist_album_date);
            this.rating = (TextView) view.findViewById(R.id.artist_album_rating_text);
            this.cover = (ImageView) view.findViewById(R.id.artist_album_cover);
            this.card = (CardView) view.findViewById(R.id.artist_album_card_view);
            this.loading = view.findViewById(R.id.loading_artist_album_card);
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

        // Fetch image from storage
        CoverCache.retrieveCover(album.getCover(), ((ItemViewHolder)holder).cover, ((ItemViewHolder)holder).loading,
                context.getApplicationContext().getDir(CoverCache.INTERNAL_DIRECTORY_ALBUM,MODE_PRIVATE));

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
