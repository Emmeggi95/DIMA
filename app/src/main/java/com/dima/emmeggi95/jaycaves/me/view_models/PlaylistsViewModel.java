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

        if(list==null){
            list = new ArrayList<>();
            list= User.playlists;

        }
            //System.out.println(" \nUSER PREFERENCES: "+User.playlists.toString()+ "\n");
       if( list.size()>0)
            playlists.postValue(list);

    }

    public LiveData<List<Playlist>> getData() {return playlists;}

    public void moveAlbum(int playlist, int x, int y){
        list.get(playlist).moveAlbum(x,y);
        playlists.setValue(list);
        User.reorderPlaylist(list.get(playlist));
        //System.out.println("View model: album playlist moved");
    }

    public void removeAlbum(int playlist, int x){
        Playlist temp= list.get(playlist);
        User.updatePlaylist(list.get(playlist), list.get(playlist).getAlbums().get(x), "REMOVE");
        temp.removeAlbum(x);
        User.reorderPlaylist(temp);
        list.get(playlist).getAlbums().clear();
        list.get(playlist).getAlbums().addAll(temp.getAlbums());
        playlists.setValue(list);

        //System.out.println("View model: album playlist deleted");
    }

    public boolean addAlbum(int playlist, Album album){
        Playlist temp = list.get(playlist);
        for (Album a: temp.getAlbums())
            if((a.getTitle().equals(album.getTitle())) && (a.getArtist().equals(album.getArtist()))) // check if album already in playlist
                return false;
        User.updatePlaylist(list.get(playlist), album, "ADD");
        User.playlists.get(playlist).addEntry(album);
        temp.addEntry(album);
        list.get(playlist).getAlbums().clear();
        list.get(playlist).getAlbums().addAll(temp.getAlbums());
        playlists.setValue(list);
        return true;
    }
}
