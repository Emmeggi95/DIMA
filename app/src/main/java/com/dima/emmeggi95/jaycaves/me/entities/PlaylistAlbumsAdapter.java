package com.dima.emmeggi95.jaycaves.me.entities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.Album;
import com.dima.emmeggi95.jaycaves.me.AlbumActivity;
import com.dima.emmeggi95.jaycaves.me.CoverCache;
import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.layout.ItemTouchHelperAdapter;
import com.dima.emmeggi95.jaycaves.me.layout.OnStartDragListener;
import com.dima.emmeggi95.jaycaves.me.view_models.PlaylistsViewModel;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class PlaylistAlbumsAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {

    Context context;
    List<Album> albums;
    OnStartDragListener onStartDragListener;

    public PlaylistAlbumsAdapter(Context context, List<Album> albums, OnStartDragListener onStartDragListener) {
        this.context = context;
        this.albums = albums;
        this.onStartDragListener = onStartDragListener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        public CardView card;
        public TextView title;
        public TextView artist;
        public TextView genre;
        public TextView year;
        public ImageView cover;
        public ProgressBar loading;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.playlist_album_card);
            title = itemView.findViewById(R.id.playlist_album_title);
            artist = itemView.findViewById(R.id.playlist_album_artist);
            genre = itemView.findViewById(R.id.playlist_album_genre);
            year = itemView.findViewById(R.id.playlist_album_year);
            cover = itemView.findViewById(R.id.playlist_album_cover);
            loading= itemView.findViewById(R.id.loading_album_playlist);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.playlist_album_card, viewGroup, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int i) {
        final Album album = albums.get(i);

        ((ItemViewHolder) holder).title.setText(album.getTitle());
        ((ItemViewHolder) holder).artist.setText(album.getArtist());
        ((ItemViewHolder) holder).genre.setText(album.getGenre1());
        ((ItemViewHolder) holder).year.setText(album.getDate());

        // Fetch image from storage
        CoverCache.retrieveCover(album.getCover(),((PlaylistAlbumsAdapter.ItemViewHolder) holder).cover, ((PlaylistAlbumsAdapter.ItemViewHolder) holder).loading,
                context.getApplicationContext().getDir(CoverCache.INTERNAL_DIRECTORY_ALBUM,MODE_PRIVATE));

        ((ItemViewHolder) holder).card.setOnClickListener(new CardView.OnClickListener(){
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

    @Override
    public void onItemDismiss(int position) {
        albums.remove(position);
        notifyItemRemoved(position);
        onStartDragListener.remove(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(albums, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(albums, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        onStartDragListener.move(fromPosition, toPosition);
        return true;
    }
}
