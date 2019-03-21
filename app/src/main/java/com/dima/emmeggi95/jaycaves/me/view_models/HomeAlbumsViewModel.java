package com.dima.emmeggi95.jaycaves.me.view_models;

import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dima.emmeggi95.jaycaves.me.Album;
import com.dima.emmeggi95.jaycaves.me.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class HomeAlbumsViewModel extends ViewModel {

    private MutableLiveData<List<Album>> albums = new MutableLiveData<>();
    private List<Album> albumList = new ArrayList<>();

    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference reference;
    private File localFile;

    public HomeAlbumsViewModel(){

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        reference = storage.getReference("Album_covers");
        database.getReference("albums").orderByKey().limitToFirst(10).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for(DataSnapshot d : data){

                    Album tempAlbum = d.getValue(Album.class);
                  /*  try {
                        localFile= File.createTempFile("album", "jpeg");
                        reference.child(tempAlbum.getCover()).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                    System.out.println("COULD NOT DOWNLOAD IMAGE!");

                            }
                        });
                    } catch (IOException e) {
                        System.out.println("COULD NOT CREATE FILE");
                    }

                    String filePath = localFile.getPath();
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    tempAlbum.setCover_file(bitmap); */

                    // Add album to list
                    albumList.add(tempAlbum);

                }

                albums.postValue(albumList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("DATABASE DENIED DOWNLOAD");
            }
        });


        /*
        // Qui dovrebbe prendere i dati dal databse, in questo caso vengono generati nel codice.
        List<HomeAlbum> albumList = new ArrayList<HomeAlbum>();
        Review animalsReview = new Review("Emmeggi95", "A masterpiece from the 70s.", "Animals is one of my favourite albums by Pink Floyd. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi interdum varius metus, at condimentum nisi consectetur at. Aliquam eu venenatis dolor. Nulla pharetra ligula elit, ut fringilla tortor tincidunt sed. Sed venenatis risus ac tellus sollicitudin fermentum. Nullam bibendum, odio id fringilla mollis, magna tortor venenatis mauris, at laoreet felis tortor eget mauris. Sed egestas turpis eu lectus faucibus mollis. Curabitur eget tincidunt ipsum. Fusce ac imperdiet eros, sagittis semper lacus. Maecenas luctus, massa a scelerisque molestie, tellus odio consequat nisl, in suscipit metus urna ultricies ligula. Fusce lobortis luctus neque, ac commodo ligula. Mauris eu ex mollis, auctor tellus vitae, vulputate nisl. Sed consectetur urna ut velit semper auctor. Vivamus rutrum mi in erat pulvinar, sed bibendum leo egestas.",
                5, "15/03/2019", 77);
        List<Review> animalsReviews = new ArrayList<Review>();
        animalsReviews.add(animalsReview);
        albumList.add(new HomeAlbum("Animals", "Pink Floyd", "Progressive Rock", "1977", 4.62, R.drawable.pinkfloyd_animals, animalsReviews));
        albumList.add(new HomeAlbum("Abbey Road", "The Beatles", "Psychedelic Rock", 5.00, R.drawable.beatles_abbey_road));
        albumList.add(new HomeAlbum("Either/Or", "Elliott Smith", "Indie Rock", 3.88, R.drawable.elliott_smith_either_or));
        albumList.add(new HomeAlbum("Paranoid", "Black Sabbath", "Heavy Metal", 4.37, R.drawable.black_sabbath_paranoid));
        albumList.add(new HomeAlbum("Selling England by the Pound", "Genesis", "Progressive Rock", 4.57, R.drawable.genesis_selling_england_by_the_pound));
        albumList.add(new HomeAlbum("London Calling", "The Clash", "Punk Rock", 3.36, R.drawable.the_clash_london_calling));
        albumList.add(new HomeAlbum("The Doors", "The Doors", "Psychedelic Rock", 4.02, R.drawable.the_doors_the_doors));
        homeAlbums.postValue(albumList);
        */
    }

    public LiveData<List<Album>> getData(){
        return albums;
    }
}
