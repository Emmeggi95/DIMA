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
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.Album;
import com.dima.emmeggi95.jaycaves.me.AlbumActivity;
import com.dima.emmeggi95.jaycaves.me.R;

import java.util.List;

public class UserReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Review> reviews;

    public UserReviewsAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView albumTitle, albumAuthor, reviewTitle, reviewRating, reviewDate;
        ImageView albumCover;
        CardView card;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.albumTitle = itemView.findViewById(R.id.album_title);
            this.albumAuthor = itemView.findViewById(R.id.album_author);
            this.reviewTitle = itemView.findViewById(R.id.review_title);
            this.reviewRating = itemView.findViewById(R.id.review_rating);
            this.reviewDate = itemView.findViewById(R.id.review_date);
            this.albumCover = itemView.findViewById(R.id.album_cover);
            this.card = itemView.findViewById(R.id.user_review_card);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_review_card, viewGroup, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final Review review = reviews.get(i);
        ItemViewHolder holder = (ItemViewHolder) viewHolder;
        Album album;

        // Get album info from DB
        // TEMP
        album = new Album();

        if(album.getTitle()==null){
            album = new Album(review.getTitle(), "1995", "Generic Artist", "Rock", "");
        }

        holder.albumTitle.setText(album.getTitle());
        holder.albumAuthor.setText(album.getArtist());
        holder.reviewTitle.setText(review.getHeadline());
        holder.reviewRating.setText(String.format("%.2f", review.getRating()));
        holder.reviewDate.setText(review.getShortDate());
        if(album.getCover()!=null && album.getCover()!=""){
            // Set cover
        }
        final Album albumRef = album;
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AlbumActivity.class);
                intent.putExtra("review", review);
                intent.putExtra("album", albumRef);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}
