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
import java.util.List;

/**
 * View Model of all release years of albums stored in our db
 */
public class YearViewModel extends ViewModel {

    private static MutableLiveData<List<String>> albumYears = new MutableLiveData<>();
    private List<String> yearsList;
    private FirebaseDatabase database;

    public YearViewModel() {

        // Fetch years
        database= FirebaseDatabase.getInstance();
        yearsList = new ArrayList<>();
        database.getReference("albums").orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                // insert genres into list
                for (DataSnapshot d : data){
                    String year= d.getValue(Album.class).getDate();
                    if (!yearsList.contains(year))
                        yearsList.add(year);

                }


                // notify change
                albumYears.postValue(yearsList);


            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // INTERNAL ERROR
            }
        });
    }

    public LiveData<List<String>> getData(){ return albumYears; }
}

