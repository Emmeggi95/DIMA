package com.dima.emmeggi95.jaycaves.me;

import android.provider.CalendarContract;
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

/**
 * Static class to keep client-side and server-side data consistent
 */
public class User {

    // ATTRIBUTES
    public static String username = "pietro@grotti";
    public static String email;
    public static List<String> likes = new ArrayList<>();
    public static final List<Playlist> playlists = new ArrayList<>();
    public static List<Review> reviews = new ArrayList<>();

    // DB REFERENCES
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


    // 1) FETCH CLIENT-SIDE METHODS

    /**
     *
     */
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

    /**
     *
     */
    public static void initPlaylists(){


        playlists.clear();

        // INIT
        Playlist favorites = new Playlist("Favorites");
        Playlist onthego= new Playlist("Onthego");
        Playlist tolisten = new Playlist("Tolisten");


        // GET ALBUMS FROM DATABASE
        fetchAlbums(favorites);
        fetchAlbums(onthego);
        fetchAlbums(tolisten);

        // ADD FULL PLAYLISTS
        playlists.add(favorites);
        playlists.add(tolisten);
        playlists.add(onthego);

    }


    /**
     *
     */
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


    // 2) UPDATE SERVER-SIDE METHODS

    /**
     *
     * @param like
     */
    public static void addLike(final String like){

        likes.add(like);
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("likes").child(username).push();
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


    /**
     *
     * @param action
     * @param album
     */
    public static void updatePlaylist(Playlist playlist, Album album, String action){

        String id = album.getTitle()+"@"+album.getArtist();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("playlists")
                .child(username).child(playlist.getName().toLowerCase()).child(id);
        switch (action){
            case "ADD": {
                ref.child("position").setValue(playlist.getAlbums().size()+1);// Insert
                break;
            }
            case "REMOVE": {
                ref.removeValue(); // Delete
                break;
            }
            default:
                System.out.println("\n UNKNOWN ACTION FOR PLAYLIST SERVER-SIDE UPDATE \n");
        }

    }


    public static void reorderPlaylist(Playlist playlist){

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("playlists")
                .child(username).child(playlist.getName().toLowerCase());
        List<Album> newList= playlist.getAlbums();
        int position =1;

        for(Album album: newList){
            String id = album.getTitle()+"@"+album.getArtist();
            ref.child(id).child("position").setValue(position); // update position
            position++;
        }
    }



    // 3) PRIVATE METHODS

    private static void fetchAlbums(final Playlist playlist){

        playlistsRef.child(username).child(playlist.getName().toLowerCase()).orderByChild("position").addValueEventListener(new ValueEventListener() {
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
