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

public class ChartViewModel extends ViewModel {

    private MutableLiveData<List<Album>> albums = new MutableLiveData<>();
    private List<Album> albumsList;
    private FirebaseDatabase database;
    private String genre;
    private String year;


    public ChartViewModel(String selectedGenre, String selectedYear) {

        genre= selectedGenre;
        year= selectedYear;
        database= FirebaseDatabase.getInstance();
        albumsList = new ArrayList<>();

    }


    /**
     * Checks if one of the @album's genres matches with @genre
     * @param album
     * @param genre
     * @return
     */
    private boolean isOfGenre(Album album, String genre) {

        String genre1= album.getGenre1();
        if (album.getGenre2() != null){
            String genre2= album.getGenre2();
            if (album.getGenre3()!=null){
                String genre3= album.getGenre3();
                return (genre.equals(genre1) || genre.equals(genre2) || genre.equals(genre3));
            }
            else
                return (genre.equals(genre1) || genre.equals(genre2));
        }
        else
            return genre.equals(genre1);
    }

    /**
     *
     * @return
     */
    public LiveData<List<Album>> getData(){ return albums; }


    /**
     *
     * @param genre
     * @param year
     */
    public void setData(final String genre, String year){

        albumsList.clear();

        if(genre.equalsIgnoreCase("All Genres")){

            if(year.equalsIgnoreCase("All Time")){

                // CASE 1: all genres and all time
                database.getReference("albums").orderByChild("score").limitToFirst(100).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                        for (DataSnapshot d : data)
                            albumsList.add(d.getValue(Album.class));
                        // notify change
                        Collections.reverse(albumsList);
                        albums.postValue(albumsList);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // INTERNAL ERROR
                    }
                });
            } else{

                // CASE 2: all genres and specific year
                database.getReference("albums").orderByChild("date").equalTo(year).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                        for (DataSnapshot d : data)
                            albumsList.add(d.getValue(Album.class));
                        // notify change
                        Collections.sort(albumsList, Album.scoreComparator);
                        albums.postValue(albumsList);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // INTERNAL ERROR
                    }
                });
            }
        } else {
            if(year.equalsIgnoreCase("All Time")){

                // CASE 3: specific genre and all time
                database.getReference("albums").orderByKey().limitToFirst(100).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                        for (DataSnapshot d : data)
                            if (isOfGenre(d.getValue(Album.class), genre))
                                albumsList.add(d.getValue(Album.class));
                        // notify change
                        Collections.sort(albumsList, Album.scoreComparator);
                        albums.postValue(albumsList);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // INTERNAL ERROR
                    }
                });
            } else {

                // CASE 4: specific genre and specific year
                database.getReference("albums").orderByChild("date").equalTo(year).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                        for (DataSnapshot d : data)
                            if (isOfGenre(d.getValue(Album.class), genre))
                                albumsList.add(d.getValue(Album.class));
                        // notify change
                        Collections.sort(albumsList, Album.scoreComparator);
                        albums.postValue(albumsList);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // INTERNAL ERROR
                    }
                });
            }
        }
    }

}
