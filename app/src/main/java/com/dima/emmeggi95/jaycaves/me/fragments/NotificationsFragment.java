package com.dima.emmeggi95.jaycaves.me.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.entities.Notification;
import com.dima.emmeggi95.jaycaves.me.entities.adapters.NotificationsAdapter;

import java.util.ArrayList;
import java.util.List;


public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private NotificationsAdapter adapter;

    private List<Notification> notifications;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Get notifications from DB (or from local list, don't know...)
        // temp
        notifications = new ArrayList<>();

        recyclerView = view.findViewById(R.id.notifications_recycler_view);
        linearLayoutManager= new LinearLayoutManager(getActivity());
        adapter = new NotificationsAdapter(getActivity(), notifications);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        return view;
    }

}
