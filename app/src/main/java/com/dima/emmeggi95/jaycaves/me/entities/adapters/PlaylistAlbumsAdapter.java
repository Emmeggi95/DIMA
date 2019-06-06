package com.dima.emmeggi95.jaycaves.me.entities.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.activities.AlbumActivity;
import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.entities.CoverCache;
import com.dima.emmeggi95.jaycaves.me.entities.db.Album;
import com.dima.emmeggi95.jaycaves.me.layout.ItemTouchHelperAdapter;
import com.dima.emmeggi95.jaycaves.me.layout.OnStartDragListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Adapter to display albums of a certain playlist
 */
public class PlaylistAlbumsAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {

    Context context;
    List<Album> albums;
    OnStartDragListener onStartDragListener;
    List<ImageView> symbols;

    public PlaylistAlbumsAdapter(Context context, List<Album> albums, OnStartDragListener onStartDragListener) {
        this.context = context;
        this.albums = albums;
        this.onStartDragListener = onStartDragListener;
        this.symbols = new ArrayList<>();
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
            symbols.add((ImageView) itemView.findViewById(R.id.sort_symbol));
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

    public void showSortSymbol(){
        for(ImageView s : symbols){
            s.setScaleX(1);
            s.setScaleY(1);
            Animation entrance = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.order_symbol_entrance);
            s.startAnimation(entrance);

        }
    }

    public void hideSortSymbol(){
        for(final ImageView s : symbols){
            //s.setScaleX(0);
            //s.setScaleY(0);
            Animation exit = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.order_symbol_exit);
            exit.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    s.setScaleX(0);
                    s.setScaleY(0);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            s.startAnimation(exit);


        }
    }
}
