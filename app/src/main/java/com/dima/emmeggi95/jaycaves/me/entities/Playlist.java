package com.dima.emmeggi95.jaycaves.me.entities;

import com.dima.emmeggi95.jaycaves.me.Album;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Playlist implements Serializable {

    private String name;
    private List<Album> albums;

    public Playlist(String name, List<Album> albums) {
        this.name = name;
        this.albums = albums;
    }

    public String getName() {
        return name;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void removeAlbum(int pos){
        System.out.println("Playlist object: album " + albums.get(pos).getTitle() + " removed.");
        albums.remove(pos);
        // Database connection...
    }

    public void moveAlbum(int fromPosition, int toPosition){
        System.out.println("Playlist object: album " + albums.get(fromPosition).getTitle() + " moved.");
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(albums, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(albums, i, i - 1);
            }
        }
        // Database connection...
    }

    public void addEntry(Album album){
        albums.add(album);
    }
}
