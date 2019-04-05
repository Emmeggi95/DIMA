package com.dima.emmeggi95.jaycaves.me.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class GenresViewModel extends ViewModel {

    private static MutableLiveData<List<String>> genres = new MutableLiveData<>();

    public GenresViewModel(){

        // Database connection...

        // Temporary list of genres
        List<String> listGenres = new ArrayList<>();
        listGenres.add("Rock");
        listGenres.add("Classical");
        listGenres.add("Hip-Hop");
        listGenres.add("Pop");
        listGenres.add("Jazz");

        genres.postValue(listGenres);

    }

    public LiveData<List<String>> getData(){ return genres; }
}
