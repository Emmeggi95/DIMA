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
import com.dima.emmeggi95.jaycaves.me.entities.Review;
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class HomeAlbumsViewModel extends ViewModel {

    private static MutableLiveData<List<Album>> albums = new MutableLiveData<>();
    private static List<Album> albumList = new ArrayList<>();
    private FirebaseDatabase database;


    public HomeAlbumsViewModel(){

        database = FirebaseDatabase.getInstance();
        database.getReference("reviews").orderByChild("creation").limitToLast(10).addValueEventListener(new ValueEventListener() {

           @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                albumList.clear();
                List<Review> reviews = new ArrayList<>();
                for (DataSnapshot d : data)
                    reviews.add(d.getValue(Review.class));
                Collections.sort(reviews, Review.dateComparator);

                for(Review r: reviews){
                   database.getReference("albums").orderByKey().equalTo(r.getTitle()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                            for(DataSnapshot d : data){
                                albumList.add(d.getValue(Album.class));
                            }
                            albums.postValue(albumList);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("DATABASE DENIED DOWNLOAD");
            }
        });




    }

    public LiveData<List<Album>> getData(){
        return albums;
    }
}
