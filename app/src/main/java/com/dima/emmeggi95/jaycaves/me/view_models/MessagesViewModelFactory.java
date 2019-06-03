package com.dima.emmeggi95.jaycaves.me.view_models;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class MessagesViewModelFactory implements ViewModelProvider.Factory {

    private String email;

    public MessagesViewModelFactory(String email) {
        this.email = email;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MessagesViewModel(email);
    }
}
