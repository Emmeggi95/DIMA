package com.dima.emmeggi95.jaycaves.me.entities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Review> reviews;

    public ReviewsAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, date, body, likes;
        CardView arrow;
        ImageView arrowImage;
        LinearLayout bodyContainer;
        Button like;

        public ItemViewHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.card_review_title);
            this.author = (TextView) view.findViewById(R.id.card_review_author);
            this.date = (TextView) view.findViewById(R.id.card_review_date);
            this.body = (TextView) view.findViewById(R.id.card_review_body);
            this.likes = (TextView) view.findViewById(R.id.card_review_likes_number);
            this.arrow = (CardView) view.findViewById(R.id.card_arrow);
            this.arrowImage = (ImageView) view.findViewById(R.id.card_review_arrow);
            this.bodyContainer = (LinearLayout) view.findViewById(R.id.card_review_body_container);
            this.like = view.findViewById(R.id.card_review_like_button);
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


        ((ItemViewHolder) holder).title.setText(review.getHeadline());
        ((ItemViewHolder) holder).author.setText(review.getAuthor());
        ((ItemViewHolder) holder).date.setText(review.getDate());
        ((ItemViewHolder) holder).body.setText(review.getBody());
        ((ItemViewHolder) holder).likes.setText(String.valueOf(review.getLikes()));

        // Hide body and set click listener
        final LinearLayout bodyContainer = ((ItemViewHolder) holder).bodyContainer;
        bodyContainer.setVisibility(View.GONE);
        final ImageView arrow = ((ItemViewHolder) holder).arrowImage;

        for (String like : User.getLikes())
            if (review.getId().equals(like)){
                ((ItemViewHolder) holder).like.setText("LIKED");
                ((ItemViewHolder) holder).like.setEnabled(false);

            }



        ((ItemViewHolder) holder).arrow.setOnClickListener(new CardView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bodyContainer.getVisibility()==View.VISIBLE){
                    bodyContainer.setVisibility(View.GONE);
                    arrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                } else {
                    bodyContainer.setVisibility(View.VISIBLE);
                    arrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                }
            }
        });

        ((ItemViewHolder) holder).like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
              final Button like_b = v.findViewById(R.id.card_review_like_button);
               like_b.setEnabled(false);
                like_b.setText("LIKED");
                ((TextView) v.findViewById(R.id.card_review_likes_number)).setText(String.valueOf(review.getLikes()+1));
                User.addLike(review.getId());


            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}
