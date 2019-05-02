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
    public static final List<Playlist> playlists = new ArrayList<>();
    public static List<Review> reviews = new ArrayList<>();
    private static DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("likes");
    private static DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");
    private static DatabaseReference playlistsRef = FirebaseDatabase.getInstance().getReference("playlists");
    private static DatabaseReference albumRef = FirebaseDatabase.getInstance().getReference("albums");



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

    public static List<String> getLikes(){
        return likes;
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void initPlaylists(){


        playlists.clear();

        // INIT
        Playlist favorites = new Playlist("favorites");
        Playlist onthego= new Playlist("onthego");
        Playlist tolisten = new Playlist("tolisten");


        // GET ALBUMS FROM DATABASE
        fetchAlbums(favorites);
        fetchAlbums(onthego);
        fetchAlbums(tolisten);

        // ADD FULL PLAYLISTS
        playlists.add(favorites);
        playlists.add(onthego);
        playlists.add(tolisten);


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


          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {
                // DO STH
          }
      });
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


    private static void fetchAlbums(final Playlist playlist){
        playlistsRef.child(username).child(playlist.getName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for (DataSnapshot d1 : data){
                    albumRef.child(d1.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            playlist.addEntry(dataSnapshot.getValue(Album.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // ERROR
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    // ERROR
            }
        });
    }
}
