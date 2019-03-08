package com.dima.emmegi95.jaycaves.sm2;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

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

        // specify an adapter
        List<HomeAlbum> albumList = new ArrayList<HomeAlbum>();
        albumList.add(new HomeAlbum(getResources().getString(R.string.home_header_title)));
        albumList.add(new HomeAlbum("Animals", "Pink Floyd", "Progressive Rock", 4.62, R.drawable.pinkfloyd_animals));
        albumList.add(new HomeAlbum("Abbey Road", "The Beatles", "Psychedelic Rock", 5.00, R.drawable.beatles_abbey_road));
        albumList.add(new HomeAlbum("Either/Or", "Elliott Smith", "Indie Rock", 3.88, R.drawable.elliott_smith_either_or));
        albumList.add(new HomeAlbum("Paranoid", "Black Sabbath", "Heavy Metal", 4.37, R.drawable.black_sabbath_paranoid));
        albumList.add(new HomeAlbum("Selling England by the Pound", "Genesis", "Progressive Rock", 4.57, R.drawable.genesis_selling_england_by_the_pound));
        albumList.add(new HomeAlbum("London Calling", "The Clash", "Punk Rock", 3.36, R.drawable.the_clash_london_calling));
        albumList.add(new HomeAlbum("The Doors", "The Doors", "Psychedelic Rock", 4.02, R.drawable.the_doors_the_doors));
        mAdapter = new HomeAlbumsAdapter(getActivity(), albumList);
        recyclerView.setAdapter(mAdapter);
        return  view;
    }

}

