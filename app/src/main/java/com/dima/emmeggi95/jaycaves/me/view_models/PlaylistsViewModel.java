package com.dima.emmeggi95.jaycaves.me.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.dima.emmeggi95.jaycaves.me.Album;
import com.dima.emmeggi95.jaycaves.me.User;
import com.dima.emmeggi95.jaycaves.me.entities.Playlist;

import java.util.ArrayList;
import java.util.List;

public class PlaylistsViewModel extends ViewModel {

    private static MutableLiveData<List<Playlist>> playlists = new MutableLiveData<>();
    private static List<Playlist> list;

    public PlaylistsViewModel() {

        if(list==null) {
            list = new ArrayList<>();

            System.out.println(" USER PREFERENCES:"+User.playlists.toString());
            list= User.playlists;



            // Prova
          /*  List<Album> albumList = new ArrayList<>();
            albumList.add(new Album("Album #1", "1995", "Artist ABC", "Rock", ""));
            albumList.add(new Album("Album #2", "1995", "Artist ABC", "Rock", ""));
            albumList.add(new Album("Album #3", "1995",  "Artist ABC", "Rock", ""));

            list.add(new Playlist("Favorites", albumList));
            list.add(new Playlist("To Listen", new ArrayList<Album>()));
            list.add(new Playlist("On the Go", new ArrayList<Album>())); */
            if (list.size()>0)
                playlists.postValue(list);
        }

    }

    public LiveData<List<Playlist>> getData() {return playlists;}

    public void moveAlbum(int playlist, int x, int y){
        list.get(playlist).moveAlbum(x,y);
        playlists.setValue(list);
        System.out.println("View model: album playlist moved");
    }

    public void removeAlbum(int playlist, int x){
        list.get(playlist).removeAlbum(x);
        playlists.setValue(list);
        System.out.println("View model: album playlist deleted");
    }

    public boolean addAlbum(int playlist, Album album){
        list.get(playlist).addEntry(album);
        playlists.setValue(list);
        return true;
    }
}
