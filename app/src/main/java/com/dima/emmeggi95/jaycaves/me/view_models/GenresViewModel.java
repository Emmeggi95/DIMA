package com.dima.emmeggi95.jaycaves.me.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * View Model of all known genres
 */
public class GenresViewModel extends ViewModel {

    private static MutableLiveData<List<String>> genres = new MutableLiveData<>();
    private List<String> genreList;

    private FirebaseDatabase database;

    public GenresViewModel(){

        // Fetch genres
        database= FirebaseDatabase.getInstance();
        genreList = new ArrayList<>();
        database.getReference("genres").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                // insert genres into list
                for (DataSnapshot d : data)
                    genreList.add(d.getKey());

                // notify change
                genres.postValue(genreList);


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // INTERNAL ERROR
            }
        });

        /*genreList.add("Rock");
        genreList.add("Classical");
        genreList.add("Hip-Hop");
        genreList.add("Pop");
        genreList.add("Jazz");
*/


    }

    public LiveData<List<String>> getData(){ return genres; }
}
