package com.dima.emmeggi95.jaycaves.me;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dima.emmeggi95.jaycaves.me.entities.Playlist;
import com.dima.emmeggi95.jaycaves.me.view_models.PlaylistsViewModel;

import java.util.ArrayList;
import java.util.List;

public class PlaylistsFragment extends Fragment {

    private View playlist1View;
    private View playlist2View;
    private View playlist3View;

    private List<Playlist> playlists;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlists, container, false);

        playlist1View = view.findViewById(R.id.playlist1);
        playlist2View = view.findViewById(R.id.playlist2);
        playlist3View = view.findViewById(R.id.playlist3);

        playlists = User.playlists;


        playlist1View.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlaylistActivity.class);
                //intent.putExtra("playlist", playlists.get(0));
                intent.putExtra("position", 0);
                startActivity(intent);
            }
        });
        playlist2View.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlaylistActivity.class);
                //intent.putExtra("playlist", playlists.get(1));
                intent.putExtra("position", 1);
                startActivity(intent);
            }
        });
        playlist3View.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlaylistActivity.class);
               // intent.putExtra("playlist", playlists.get(2));
                intent.putExtra("position", 2);
                startActivity(intent);
            }
        });

        return view;
    }
}
