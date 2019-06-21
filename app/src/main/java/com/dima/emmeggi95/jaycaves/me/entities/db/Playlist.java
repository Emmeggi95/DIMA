package com.dima.emmeggi95.jaycaves.me.entities.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * POJO of a playlist
 */
public class Playlist implements Serializable {

    private String dbName, name;
    private List<Album> albums;

    public Playlist(String dbName, String name, List<Album> albums) {
        this.dbName = dbName;
        this.name = name;
        this.albums = albums;
    }

    public Playlist(String dbName, String name){
        this.dbName = dbName;
        this.name = name;
        albums= new ArrayList<>();
    }

    public String getDbName() {
        return dbName;
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

    }

    public boolean addEntry(Album album){

        String idAlbum = album.getTitle()+"@"+album.getArtist();
        for (Album a: albums){
            String idA = a.getTitle()+"@"+a.getArtist();
            if( idA.equalsIgnoreCase(idAlbum))
                return false;
        }
        albums.add(album);
        return true;

    }

    @Override
    public String toString() {
        return "Playlist: " + dbName +
                ", albums=" + albums +
                '}';
    }
}
