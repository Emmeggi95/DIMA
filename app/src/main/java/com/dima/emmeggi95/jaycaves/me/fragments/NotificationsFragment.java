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

import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.entities.CustomNotification;
import com.dima.emmeggi95.jaycaves.me.entities.NotificationLike;
import com.dima.emmeggi95.jaycaves.me.entities.adapters.NotificationsAdapter;
import com.dima.emmeggi95.jaycaves.me.view_models.NotificationsViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private NotificationsAdapter adapter;
    private NotificationsViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        recyclerView = view.findViewById(R.id.notifications_recycler_view);
        linearLayoutManager= new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        // load data from ViewModel
        viewModel = ViewModelProviders.of(getActivity()).get(NotificationsViewModel.class);
        adapter = new NotificationsAdapter(getActivity(), new ArrayList<NotificationLike>());
        recyclerView.setAdapter(adapter);

        final Observer<List<NotificationLike>> observer = new Observer<List<NotificationLike>>() {
            @Override
            public void onChanged(@Nullable List<NotificationLike> notifications) {
                adapter = new NotificationsAdapter(getActivity(), notifications);
                recyclerView.setAdapter(adapter);
            }
        };

        viewModel.getData().observe(this, observer);




        return view;
    }

}
