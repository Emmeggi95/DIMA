package com.dima.emmeggi95.jaycaves.me.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.entities.db.Album;
import com.dima.emmeggi95.jaycaves.me.entities.adapters.ChartAlbumsAdapter;
import com.dima.emmeggi95.jaycaves.me.view_models.ChartViewModel;
import com.dima.emmeggi95.jaycaves.me.view_models.ChartViewModelFactory;
import com.dima.emmeggi95.jaycaves.me.view_models.GenresViewModel;
import com.dima.emmeggi95.jaycaves.me.view_models.YearViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows charts
 */
public class ChartsFragment extends Fragment {

    RecyclerView chartsRecyclerView;
    RecyclerView.Adapter chartsAdapter;
    RecyclerView.LayoutManager chartsLayoutManager;

    Spinner genresSpinner;
    Spinner yearsSpinner;

    GenresViewModel genresViewModel;
    YearViewModel yearsViewModel;
    ChartViewModel chartViewModel;

    List<String> years;
    List<String> genres;
    List<Album> albums;


    String selectedGenre;
    String selectedYear;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_charts, container, false);

        // Set the recycler view
        chartsRecyclerView = view.findViewById(R.id.charts_recycler_view);
        chartsLayoutManager = new LinearLayoutManager(getActivity());
        chartsRecyclerView.setLayoutManager(chartsLayoutManager);



        // Genres spinner init
        genresSpinner = view.findViewById(R.id.genre_spinner);
        genresViewModel = ViewModelProviders.of(getActivity()).get(GenresViewModel.class); // Get genres from ViewModel
        genres = new ArrayList<>();
        final Observer genresObserver = new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> genresList) {
                genres.clear();
                genres.add(getResources().getString(R.string.all_genres));
                genres.addAll(genresList);
                ArrayAdapter<String> genresArray = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, genres);
                genresSpinner.setAdapter(genresArray);
            }
        };
        genresViewModel.getData().observe(this, genresObserver);
        genresSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { // Set listener
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGenre = parent.getItemAtPosition(position).toString();
                if(selectedYear!=null) // avoid duplication
                    chartViewModel.setData(selectedGenre,selectedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        // Years spinner init
        yearsSpinner = view.findViewById(R.id.year_spinner);
        yearsViewModel = ViewModelProviders.of(getActivity()).get(YearViewModel.class);
        years= new ArrayList<>();
        final Observer yearsObserver = new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> yearsList) {
                years.clear();
                years.add(getResources().getString(R.string.all_years));
                years.addAll(yearsList);
                ArrayAdapter<String> yearsArray = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, years);
                yearsSpinner.setAdapter(yearsArray);
            }
        };
        yearsViewModel.getData().observe(this, yearsObserver);
        yearsSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { // Set listener
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = parent.getItemAtPosition(position).toString();
                chartViewModel.setData(selectedGenre,selectedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Init ChartViewModel
        albums= new ArrayList<>();
        chartViewModel = ViewModelProviders.of(getActivity(),
                new ChartViewModelFactory(selectedYear,selectedGenre)).get(ChartViewModel.class);

        final Observer observer = new Observer<List<Album>>() {
            @Override
            public void onChanged(@Nullable List<Album> chartAlbums) {
                // set animation for the recyclerview
                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_list);
                chartsRecyclerView.setLayoutAnimation(animation);
                chartsAdapter = new ChartAlbumsAdapter(getActivity(), chartAlbums);
                chartsRecyclerView.setAdapter(chartsAdapter);
            }
        };

        chartViewModel.getData().observe(this, observer);


        return view;
    }




}
