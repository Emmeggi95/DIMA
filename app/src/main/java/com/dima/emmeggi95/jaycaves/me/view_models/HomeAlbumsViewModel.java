package com.dima.emmeggi95.jaycaves.me.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.dima.emmeggi95.jaycaves.me.entities.db.Album;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * View Model of albums in Home fragment
 */
public class HomeAlbumsViewModel extends ViewModel {

    private static MutableLiveData<List<Album>> albums = new MutableLiveData<>();
    private static List<Album> albumList = new ArrayList<>();
    private FirebaseDatabase database;


    public HomeAlbumsViewModel(){

        database = FirebaseDatabase.getInstance();
        database.getReference("albums").orderByChild("reviewed").limitToLast(10).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                albumList.clear();
                for (DataSnapshot d: data)
                    albumList.add(d.getValue(Album.class));
                Collections.reverse(albumList);
                albums.postValue(albumList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



     /*   database.getReference("reviews").orderByChild("creation").limitToLast(10).addValueEventListener(new ValueEventListener() {

           @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                albumList.clear();
                List<Review> reviews = new ArrayList<>();
                for (DataSnapshot d : data)
                    reviews.add(d.getValue(Review.class));
                Collections.sort(reviews, Review.dateComparator);

                for(Review r: reviews){
                   database.getReference("albums").orderByKey().equalTo(r.getTitle()).addListenerForSingleValueEvent(new ValueEventListener() {
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
        }); */




    }

    public LiveData<List<Album>> getData(){
        return albums;
    }
}
