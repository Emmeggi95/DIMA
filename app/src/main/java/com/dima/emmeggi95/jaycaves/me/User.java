package com.dima.emmeggi95.jaycaves.me;

import android.support.annotation.NonNull;

import com.dima.emmeggi95.jaycaves.me.entities.Playlist;
import com.dima.emmeggi95.jaycaves.me.entities.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class User {

    public static String username = "pietro@grotti";
    public static String email;
    public static List<String> likes = new ArrayList<>();
    public static List<Playlist> playlists;
    public static List<Review> reviews = new ArrayList<>();
    private static DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("likes");
    private static DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");



    // STATIC GETTERS & SETTERS

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        User.username = username;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        User.email = email;
    }

    public static List<Review> getReviews() {
        return reviews;
    }

    public static void setReviews(List<Review> reviews) {
        User.reviews = reviews;
    }


// STATIC METHODS

    public static void initReviews(){
        reviewsRef.orderByChild("author").equalTo(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                reviews.clear();
                for (DataSnapshot d1: data)
                    reviews.add(d1.getValue(Review.class));
                System.out.println("FETCHED REVIEWS: "+ reviews.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public static void initLikes(){
        // check if featured review is already liked by user

      likesRef.child(username).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              Iterable<DataSnapshot> data = dataSnapshot.getChildren();
              likes.clear();
              for (DataSnapshot d1: data)
                  likes.add(d1.getValue(String.class));
              System.out.println("FETCHED LIKES: "+ likes.toString());

          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {
                // DO STH
          }
      });
        /*

                reviewsRef.orderByChild("title").equalTo(album.getTitle()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                        for (DataSnapshot d2: data){
                            for (String id: revId)
                                if (id.equals(d2.getKey()) && d2.getValue(Review.class).getAuthor().equals(featuredReview.getAuthor())){
                                    likeButton.setText("LIKED");
                                    likeButton.setEnabled(false);
                                }

                        }
                    }
 */
    }

    public static List<String> getLikes(){
        return likes;
    }

    public static void addLike(final String like){

        likes.add(like);
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("likes").child("pietro@grotti").push(); // need to insert username FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
        ref.setValue(like);

        //Update likes on db
        reviewsRef.orderByKey().equalTo(like).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for(DataSnapshot d: data){
                        // UPDATE NUMBER OF LIKES
                        Review review = d.getValue(Review.class);
                        reviewsRef.child(d.getKey()).child("likes").setValue(review.getLikes() + 1);

                    }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    likes.remove(like);
            }
        });
    }



}
