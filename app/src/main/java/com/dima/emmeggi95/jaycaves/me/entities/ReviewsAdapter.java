package com.dima.emmeggi95.jaycaves.me.entities;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.User;
import com.dima.emmeggi95.jaycaves.me.UserActivity;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Review> reviews;
    private int max;
    private int startColor;
    private int endColor;
    private int deltaColor;

    public ReviewsAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
        this.max = 0;
        for(Review r : reviews){
            if(r.getLikes()>max)
                max = r.getLikes();
        }
        startColor = context.getResources().getColor(R.color.star_color);
        endColor = context.getResources().getColor(R.color.end_color);
        deltaColor = endColor - startColor;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, date, body, likes, like_liked;
        CardView card;
        ImageView arrowImage, like;
        LinearLayout bodyContainer;
        View header;

        public ItemViewHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.card_review_title);
            this.author = (TextView) view.findViewById(R.id.card_review_author);
            this.date = (TextView) view.findViewById(R.id.card_review_date);
            this.body = (TextView) view.findViewById(R.id.card_review_body);
            this.likes = (TextView) view.findViewById(R.id.card_review_likes);
            this.arrowImage = (ImageView) view.findViewById(R.id.card_review_arrow);
            this.bodyContainer = (LinearLayout) view.findViewById(R.id.card_review_body_container);
            this.like = view.findViewById(R.id.card_review_heart);
            this.like_liked = view.findViewById(R.id.card_review_like_message);
            this.header = view.findViewById(R.id.info);
            this.card = view.findViewById(R.id.review_card);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_card, parent,false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Review review = reviews.get(position);

        ((ItemViewHolder) holder).card.setCardBackgroundColor(calculateGradient(review.getLikes()));
        ((ItemViewHolder) holder).title.setText(review.getHeadline());
        ((ItemViewHolder) holder).author.setText(review.getAuthor());
        ((ItemViewHolder) holder).author.setOnClickListener(new TextView.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserActivity.class);
                intent.putExtra("username", review.getAuthor());
                intent.putExtra("email", review.getUserEmail());
                context.startActivity(intent);
            }
        });
        ((ItemViewHolder) holder).date.setText(review.getShortDate());
        ((ItemViewHolder) holder).body.setText(review.getBody());
        ((ItemViewHolder) holder).likes.setText(String.valueOf(review.getLikes()) + " " + context.getResources().getString(R.string.likes));

        // Hide body and set click listener
        final LinearLayout bodyContainer = ((ItemViewHolder) holder).bodyContainer;
        bodyContainer.setVisibility(View.GONE);
        final ImageView arrow = ((ItemViewHolder) holder).arrowImage;

        for (String like : User.likes)
            if (review.getId().equals(like)){
                ((ItemViewHolder) holder).like.setImageResource(R.drawable.ic_favorite_black_24dp);
                ((ItemViewHolder) holder).like_liked.setText(context.getResources().getString(R.string.liked));

            }



        ((ItemViewHolder) holder).header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bodyContainer.getVisibility()==View.VISIBLE){
                    bodyContainer.setVisibility(View.GONE);
                    Animation rotate = AnimationUtils.loadAnimation(context, R.anim.rotate180clockwise);
                    arrow.startAnimation(rotate);
                    arrow.setRotation(180);
                } else {
                    bodyContainer.setVisibility(View.VISIBLE);
                    Animation rotate = AnimationUtils.loadAnimation(context, R.anim.rotate180anticlockwise);
                    arrow.startAnimation(rotate);
                    arrow.setRotation(0);
                }
            }
        });

            final ImageView heart = ((ItemViewHolder) holder).like;
            final TextView message = ((ItemViewHolder) holder).like_liked;
        ((ItemViewHolder) holder).like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                heart.setImageResource(R.drawable.ic_favorite_black_24dp);
                Animation bump = AnimationUtils.loadAnimation(context, R.anim.bump);
                heart.startAnimation(bump);
                message.setText(context.getResources().getString(R.string.liked));
                User.addLike(review.getId());


            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    private int calculateGradient(int likes){
        if(max==0) return endColor;
        return startColor + deltaColor * (likes/max);
    }
}
