package com.dima.emmeggi95.jaycaves.me;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.dima.emmeggi95.jaycaves.me.entities.FreshAlbumsAdapter;
import com.dima.emmeggi95.jaycaves.me.view_models.FreshAlbumsViewModel;

import java.util.List;

public class FreshFragment extends Fragment {

    private FreshAlbumsViewModel freshAlbumsViewModel;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    public static FreshFragment newInstance() {
        return new FreshFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fresh, container, false);

        recyclerView = view.findViewById(R.id.fresh_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // set animation for the recyclerview
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_list);
        recyclerView.setLayoutAnimation(animation);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        freshAlbumsViewModel = ViewModelProviders.of(this).get(FreshAlbumsViewModel.class);
        final Observer<List<Album>> observer = new Observer<List<Album>>() {
            @Override
            public void onChanged(@Nullable List<Album> albums) {
                adapter = new FreshAlbumsAdapter(getActivity(), albums);
                recyclerView.setAdapter(adapter);
            }
        };
        freshAlbumsViewModel.getData().observe(this, observer);
    }

}
