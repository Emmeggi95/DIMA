package com.dima.emmeggi95.jaycaves.me.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.dima.emmeggi95.jaycaves.me.entities.db.AccountPreference;
import com.dima.emmeggi95.jaycaves.me.entities.db.Album;
import com.dima.emmeggi95.jaycaves.me.entities.db.ChatPreview;
import com.dima.emmeggi95.jaycaves.me.entities.db.Playlist;
import com.dima.emmeggi95.jaycaves.me.entities.db.Review;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Static class to keep client-side and server-side data consistent
 */
public class User {

    // ATTRIBUTES
    public static String username; // ="pietro@grotti";
    public static String email; // = "pietro@grotti";
    public static String uid;
    public static String cover_photo_id;
    public static Bitmap cover_image;
    public static File localFile;
    public static List<String> likes = new ArrayList<>();
    public static final List<Playlist> playlists = new ArrayList<>();
    public static List<Review> reviews = new ArrayList<>();
    public static List<ChatPreview> chats = new ArrayList<>();
    private static List<ChatPreview> mychats = new ArrayList<>();
    private static List<ChatPreview> otherchats= new ArrayList<>();
    public static HashMap<String,ArrayList<String>> friends = new HashMap<>(); // for performance improvement
    public static HashMap<String, Album> reviewedAlbums = new HashMap<>(); // for performance improvement

    // DB REFERENCES
    private static DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("likes");
    private static DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");
    private static DatabaseReference playlistsRef = FirebaseDatabase.getInstance().getReference("playlists");
    private static DatabaseReference albumRef = FirebaseDatabase.getInstance().getReference("albums");
    private static DatabaseReference preferenceRef = FirebaseDatabase.getInstance().getReference("preferences");
    private static DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("chats");
    private static StorageReference storageReference = FirebaseStorage.getInstance().getReference("User_covers");
    private static DatabaseReference notificationReference = FirebaseDatabase.getInstance().getReference("notifications");


// STATIC SETTERS


    public static void setUsername(String username) {
        User.username = username;
    }

    public static void setEmail(String email) {
        User.email = email;
    }

    public static void setUid(String uid){
        User.uid= uid;
    }


// STATIC CUSTOM GETTERS

    public static Review getReviewFromId(String id){
        for (Review r: reviews)
            if(r.getId().equals(id))
                return r;
        return null; // could not find review
    }




// STATIC METHODS


    // 1) FETCH CLIENT-SIDE METHODS

    /**
     *
     */
    public static void initReviews(){
        reviewsRef.orderByChild("userEmail").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                reviews.clear();
                reviewedAlbums.clear();
                for (DataSnapshot d1: data) {
                    final Review recent = d1.getValue(Review.class);
                    reviews.add(recent);
                    albumRef.orderByKey().equalTo(recent.getTitle()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                            for (DataSnapshot d1: data) {
                                reviewedAlbums.put(recent.getId(), d1.getValue(Album.class)); // fetch albums to avoid downloads
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Fetches user playlists
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
     * Fetches user likes. This is used to avoid users to like same review multiple times
     */
    public static void initLikes(){

        // check if featured review is already liked by user

      likesRef.child(uid).addValueEventListener(new ValueEventListener() {
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

    /**
     * Retrieves user's personal cover picture and username.
     * @param cover
     */
    public static void initPreferences(final ImageView cover) {

        preferenceRef.orderByKey().equalTo(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                    for (DataSnapshot d : data) {
                        AccountPreference preference= d.getValue(AccountPreference.class);
                        cover_photo_id = preference.getCoverphoto();
                        username=preference.getUsername();

                    }

                        if (cover_photo_id != null) {
                            try {
                                localFile = File.createTempFile("images", "jpeg");
                                storageReference.child(cover_photo_id).getFile(localFile) // SET COVER
                                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                cover_image= BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                                cover.setImageBitmap(cover_image);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("\nFAILED TO DOWNLOAD COVER\n");
                                    }
                                });


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
    }

    /**
     * Retrieves user's already started chats.
     */
    public static void initChats(){

        // SELF INITIATED CHATS
        chatsRef.orderByChild("user_1").equalTo(User.uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                mychats.clear();
                for (DataSnapshot d : data) {
                    ChatPreview m_chat = d.getValue(ChatPreview.class);
                    mychats.add(m_chat);
                    if (friends.get(m_chat.getChatId()) == null){ // not registered friend
                        preferenceRef.orderByKey().equalTo(m_chat.getUser_2()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                                ArrayList<String> info = new ArrayList<>();
                                for (DataSnapshot d : data) {
                                    AccountPreference pref = d.getValue(AccountPreference.class);
                                    info.add(pref.getUsername());
                                    info.add(pref.getCoverphoto());
                                    friends.put(d.getKey(), info);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });

                    }


                }
                chats.clear();
                chats.addAll(mychats);
                chats.addAll(otherchats);
                Collections.sort(chats, ChatPreview.dateComparator);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        // OTHER CHATS
        chatsRef.orderByChild("user_2").equalTo(User.uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                otherchats.clear();
                for (DataSnapshot d : data) {
                    ChatPreview o_chat = d.getValue(ChatPreview.class);
                    otherchats.add(o_chat);
                    if (friends.get(o_chat.getChatId()) == null) { // not registered friend
                        preferenceRef.orderByKey().equalTo(o_chat.getUser_1()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                                ArrayList<String> info = new ArrayList<>();
                                for (DataSnapshot d : data) {
                                    AccountPreference pref = d.getValue(AccountPreference.class);
                                    info.add(pref.getUsername());
                                    info.add(pref.getCoverphoto());
                                    friends.put(d.getKey(), info);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }
                chats.clear();
                chats.addAll(mychats);
                chats.addAll(otherchats);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });


    }


    // 2) UPDATE SERVER-SIDE METHODS

    /**
     * Registers on server the like and generates the notification
     * @param  review liked review
     */
    public static void addLike(final Review review){

        likes.add(review.getId());
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("likes").child(uid).push();
        ref.setValue(review.getId());

        //Update likes on db
        reviewsRef.orderByKey().equalTo(review.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for(DataSnapshot d: data){
                        // UPDATE NUMBER OF LIKES
                        Review currentReview = d.getValue(Review.class);
                        reviewsRef.child(d.getKey()).child("likes").setValue(currentReview.getLikes() + 1);

                    }
                preferenceRef.orderByChild("username").equalTo(review.getAuthor()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                        String id = "";
                        for(DataSnapshot d: data) {
                             id = d.getKey();
                        }
                        Date currenTime = Calendar.getInstance().getTime();
                        SimpleDateFormat sdt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        NotificationLike newNote = new NotificationLike("", sdt.format(currenTime), false, review.getId(), review.getTitle(), id, User.username);
                        DatabaseReference newRef = notificationReference.child(id).push();
                        newRef.setValue(newNote);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    likes.remove(review.getId());
            }
        });
    }


    /**
     *
     * @param action
     * @param album
     */
    public static void updatePlaylist(final Playlist playlist, final Album album, String action){

        String id = album.getTitle()+"@"+album.getArtist();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("playlists")
                .child(uid).child(playlist.getName().toLowerCase()).child(id);
        switch (action){
            case "ADD": {
                ref.child("position").setValue(playlist.getAlbums().size());// Insert
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




    /**
     *
     * @param playlist
     */
    public static void reorderPlaylist(Playlist playlist){

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("playlists")
                .child(uid).child(playlist.getName().toLowerCase());
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

        playlistsRef.child(uid).child(playlist.getName().toLowerCase()).orderByChild("position").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for (DataSnapshot d1 : data){
                    //playlist.getAlbums().clear();
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
