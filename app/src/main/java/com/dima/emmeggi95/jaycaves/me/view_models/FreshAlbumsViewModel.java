package com.dima.emmeggi95.jaycaves.me.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.dima.emmeggi95.jaycaves.me.Album;

import java.util.ArrayList;
import java.util.List;

public class FreshAlbumsViewModel extends ViewModel {

    private static MutableLiveData<List<Album>> albums = new MutableLiveData<>();

    public FreshAlbumsViewModel() {

        // Databse connection...

        // Temporary list of albums
        List<Album> albumList = new ArrayList<>();
        albumList.add(new Album("Album #1", "1995", 4.17, "Artist ABC", "Rock", ""));
        albumList.add(new Album("Album #2", "1995", 4.17, "Artist ABC", "Rock", ""));
        albumList.add(new Album("Album #3", "1995", 4.17, "Artist ABC", "Rock", ""));

        albums.postValue(albumList);
    }

    public LiveData<List<Album>> getData(){
        return albums;
    }
}
