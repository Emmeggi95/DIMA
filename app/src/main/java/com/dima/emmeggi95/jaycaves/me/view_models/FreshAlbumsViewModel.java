package com.dima.emmeggi95.jaycaves.me.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.dima.emmeggi95.jaycaves.me.Album;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FreshAlbumsViewModel extends ViewModel {

    private static MutableLiveData<List<Album>> albums = new MutableLiveData<>();
    private static List<Album> albumList = new ArrayList<>();

    public FreshAlbumsViewModel() {


        albumList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("albums").orderByChild("creation").limitToLast(10).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                albumList.clear();
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for(DataSnapshot d : data)
                    albumList.add(d.getValue(Album.class));
                Collections.reverse(albumList);
                albums.postValue(albumList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public LiveData<List<Album>> getData(){
        return albums;
    }
}
