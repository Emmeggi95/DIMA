package com.dima.emmeggi95.jaycaves.me.view_models;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class ChartViewModelFactory implements ViewModelProvider.Factory {

    private String year;
    private String genre;

    public ChartViewModelFactory(String year, String genre) {
        this.year = year;
        this.genre = genre;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ChartViewModel(genre,year);
    }
}
