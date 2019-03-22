package com.dima.emmeggi95.jaycaves.me;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dima.emmeggi95.jaycaves.me.entities.HomeAlbumsAdapter;
import com.dima.emmeggi95.jaycaves.me.view_models.HomeAlbumsViewModel;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    private HomeAlbumsViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.home_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // load data from ViewModel
        viewModel = ViewModelProviders.of(getActivity()).get(HomeAlbumsViewModel.class);

        final Observer<List<Album>> observer = new Observer<List<Album>>() {
            @Override
            public void onChanged(@Nullable List<Album> homeAlbums) {
                adapter = new HomeAlbumsAdapter(getActivity(), homeAlbums);
                recyclerView.setAdapter(adapter);
            }
        };

        viewModel.getData().observe(this, observer);

        return  view;
    }

}

