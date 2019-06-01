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

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to display reviews
 */
public class ReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Review> reviews;
    private int max;
    private int startColor;
    private int endColor;
    private int deltaColor;
    private List<LinearLayout> containers;
    private List<ImageView> arrows;

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
        containers = new ArrayList<>();
        arrows = new ArrayList<>();
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
            arrows.add(arrowImage);
            this.bodyContainer = (LinearLayout) view.findViewById(R.id.card_review_body_container);
            containers.add(bodyContainer);
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final Review review = reviews.get(position);
        ItemViewHolder h = (ItemViewHolder) holder; 

        h.likes.setTextColor(calculateGradient(review.getLikes()));
        h.title.setText(review.getHeadline());
        h.author.setText(review.getAuthor());
        h.author.setOnClickListener(new TextView.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserActivity.class);
                intent.putExtra("username", review.getAuthor());
                intent.putExtra("email", review.getUserEmail());
                context.startActivity(intent);
            }
        });
        h.date.setText(review.getShortDate());
        h.body.setText(review.getBody());
        h.likes.setText(String.valueOf(review.getLikes()) + " " + context.getResources().getString(R.string.likes));

        // Hide body and set click listener
        final LinearLayout bodyContainer = h.bodyContainer;
        bodyContainer.setVisibility(View.GONE);
        final ImageView arrow = h.arrowImage;

        for (String like : User.likes)
            if (review.getId().equals(like)){
                h.like.setImageResource(R.drawable.ic_favorite_black_24dp);
                h.like_liked.setText(context.getResources().getString(R.string.liked));

            }

        h.header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bodyContainer.getVisibility()==View.VISIBLE){
                    hideContainer(bodyContainer, arrow);
                } else {
                    setContainerShown(position);
                }
            }
        });

            final ImageView heart = h.like;
            final TextView message = h.like_liked;
        h.like.setOnClickListener(new View.OnClickListener() {
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

    private void hideContainer(LinearLayout bodyContainer, ImageView arrow){
        if(bodyContainer.getVisibility()==View.VISIBLE){
            bodyContainer.setVisibility(View.GONE);
            Animation rotate = AnimationUtils.loadAnimation(context, R.anim.rotate180clockwise);
            arrow.startAnimation(rotate);
            arrow.setRotation(180);
        }
    }

    private void showContainer(LinearLayout bodyContainer, ImageView arrow){
        if(bodyContainer.getVisibility()==View.GONE){
            bodyContainer.setVisibility(View.VISIBLE);
            Animation rotate = AnimationUtils.loadAnimation(context, R.anim.rotate180anticlockwise);
            arrow.startAnimation(rotate);
            arrow.setRotation(0);
        }
    }

    private void setContainerShown(int position){
        for(int i = 0; i<getItemCount(); i++){
            if(i==position){
                showContainer(containers.get(i), arrows.get(i));
            } else {
                hideContainer(containers.get(i), arrows.get(i));
            }
        }
    }
}
