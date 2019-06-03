package com.dima.emmeggi95.jaycaves.me;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dima.emmeggi95.jaycaves.me.entities.ChatPreview;
import com.dima.emmeggi95.jaycaves.me.entities.ChatPreviewsAdapter;
import com.dima.emmeggi95.jaycaves.me.view_models.ChatsViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ChatPreviewsAdapter adapter;

    List<ChatPreview> chats;
    ChatsViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Download chats info from DB
        // temp
        chats = new ArrayList<>();
        viewModel = ViewModelProviders.of(getActivity()).get(ChatsViewModel.class);

        recyclerView = view.findViewById(R.id.chat_recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        adapter = new ChatPreviewsAdapter(getActivity(), chats);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        Observer observer = new Observer<List<ChatPreview>>() {
            @Override
            public void onChanged(@Nullable List<ChatPreview> chatPreviews) {
                chats.clear();
                chats.addAll(chatPreviews);
                adapter.notifyDataSetChanged();
            }
        };

        viewModel.getData().observe(this, observer);

        return view;
    }

}
