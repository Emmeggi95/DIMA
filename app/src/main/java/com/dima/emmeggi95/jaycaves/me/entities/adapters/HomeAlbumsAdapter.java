package com.dima.emmeggi95.jaycaves.me.entities.adapters;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
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

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;


/**
 * This class is used to inflate the content in the RecyclerView in the Home Page.
 */
public class HomeAlbumsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Album> albumList;


    /**
     * @param mContext
     * @param albumList
     */
    public HomeAlbumsAdapter(Context mContext, List<Album> albumList) {
        this.context = mContext;
        this.albumList = albumList;

    }

    /**
     * Holder to manage a single item in the RecyclerView
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title, author, genre, rating;
        public ImageView cover;
        public List<ImageView> stars;
        public CardView card, coverCard;
        public ProgressBar loading;

        public ItemViewHolder(View view) {
            super(view);
            title= view.findViewById(R.id.text_album_title);
            author = (TextView) view.findViewById(R.id.text_author);
            genre = (TextView) view.findViewById(R.id.text_genre);
            cover = (ImageView) view.findViewById(R.id.image_cover);
            rating = (TextView) view.findViewById(R.id.text_rating);
            stars = new ArrayList<ImageView>();
            stars.add((ImageView) view.findViewById(R.id.star_1));
            stars.add((ImageView) view.findViewById(R.id.star_2));
            stars.add((ImageView) view.findViewById(R.id.star_3));
            stars.add((ImageView) view.findViewById(R.id.star_4));
            stars.add((ImageView) view.findViewById(R.id.star));
            card = (CardView) view.findViewById(R.id.card);
            coverCard = view.findViewById(R.id.cover_card);
            loading= (ProgressBar) view.findViewById(R.id.loading_cover);

        }
    }

    /**
     * @param parent
     * @param i
     * @return
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_album_card, parent, false);
        return new ItemViewHolder(itemView);
    }

    /**
     * Set the parameters of each item from the corresponding Album in albumList
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Album album = albumList.get(position);
        final ItemViewHolder h = (ItemViewHolder) holder;
        
        h.title.setText(album.getTitle());
        h.author.setText(album.getArtist());
        h.genre.setText(album.getGenre1());
        h.rating.setText(String.format("%.2f", album.getScore()));

       // Fetch image from storage
        CoverCache.retrieveCover(album.getCover(),h.cover, h.loading,
                context.getApplicationContext().getDir(CoverCache.INTERNAL_DIRECTORY_ALBUM,MODE_PRIVATE));


        //Set rating stars
        if(album.getScore()==0.0){
            h.stars.get(1).setVisibility(GONE);
            h.stars.get(2).setVisibility(GONE);
            h.stars.get(3).setVisibility(GONE);
            h.stars.get(4).setVisibility(GONE);
            h.rating.setText(context.getResources().getString(R.string.not_available));
        } else {
            int integerScore = (int) album.getScore();
            int i;
            for (i = 0; i < integerScore; i++) {
                h.stars.get(i).setImageResource(R.drawable.ic_star_24dp);
            }
            float decimalPart = (float) album.getScore() - integerScore;
            if (decimalPart >= 0.5) {
                h.stars.get(i).setImageResource(R.drawable.ic_star_half_24dp);
            }
        }

        //Set listener to open AlbumActivity
        h.card.setOnClickListener(new CardView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AlbumActivity.class);
                intent.putExtra("album", albumList.get(position));
                intent.putExtra("return_radius", context.getResources().getDimension(R.dimen.card_radius));
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, h.coverCard, "album_cover");

                context.startActivity(intent, options.toBundle());
            }
        });
    }

    private Album getItem(int position) {
        return albumList.get(position);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }




}