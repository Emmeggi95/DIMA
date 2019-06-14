package com.dima.emmeggi95.jaycaves.me.fragments;

import android.animation.ObjectAnimator;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.transition.Transition;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;

import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.activities.UserActivity;
import com.dima.emmeggi95.jaycaves.me.entities.db.Album;
import com.dima.emmeggi95.jaycaves.me.entities.adapters.HomeAlbumsAdapter;
import com.dima.emmeggi95.jaycaves.me.view_models.HomeAlbumsViewModel;

import java.util.List;

/**
 * Fragment that appears in the MainActivity once user has successfully registered/logged in
 */
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

        // set animation for the recyclerview
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_list);
        recyclerView.setLayoutAnimation(animation);

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

        // TEMP: NEW ACTIVITY BUTTON
        Button newActivtyButton = view.findViewById(R.id.revenant_button);
        if(newActivtyButton!=null) {
            newActivtyButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UserActivity.class);
                    startActivity(intent);
                }
            });
        }

        return  view;
    }


}

