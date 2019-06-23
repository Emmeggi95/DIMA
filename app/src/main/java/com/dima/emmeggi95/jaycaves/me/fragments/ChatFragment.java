package com.dima.emmeggi95.jaycaves.me.fragments;

import android.arch.lifecycle.LiveData;
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
import com.dima.emmeggi95.jaycaves.me.entities.NotificationLike;
import com.dima.emmeggi95.jaycaves.me.entities.User;
import com.dima.emmeggi95.jaycaves.me.entities.adapters.NotificationsAdapter;
import com.dima.emmeggi95.jaycaves.me.entities.db.ChatPreview;
import com.dima.emmeggi95.jaycaves.me.entities.adapters.ChatPreviewsAdapter;
import com.dima.emmeggi95.jaycaves.me.view_models.ChatPreviewsViewModel;
import com.dima.emmeggi95.jaycaves.me.view_models.NotificationsViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ChatPreviewsAdapter adapter;
    List<ChatPreview> chats;
    ChatPreviewsViewModel viewModel;
    View noChats;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.chat_recycler_view);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        viewModel = ViewModelProviders.of(getActivity()).get(ChatPreviewsViewModel.class);
        adapter = new ChatPreviewsAdapter(getActivity(), new ArrayList<ChatPreview>());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        noChats = view.findViewById(R.id.no_chat);

        final Observer<List<ChatPreview>> observer = new Observer<List<ChatPreview>>() {
            @Override
            public void onChanged(@Nullable List<ChatPreview> chats) {
                adapter = new ChatPreviewsAdapter(getActivity(), chats);
                recyclerView.setAdapter(adapter);
                if(chats.size()>0){
                    noChats.setVisibility(View.GONE);
                } else {
                    noChats.setVisibility(View.VISIBLE);
                }
            }
        };

        viewModel.getData().observe(this, observer);

        return view;
    }



}
