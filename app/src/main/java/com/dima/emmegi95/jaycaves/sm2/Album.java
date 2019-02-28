package com.dima.emmegi95.jaycaves.sm2;

public class Album {

    private long id;
    private String title;
    private String year;
    private String month;
    private String artist;
    private String genre;
    private String cover;


    public Album(long id, String title, String year, String month, String artist, String genre, String cover) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.month = month;
        this.artist = artist;
        this.genre = genre;
        this.cover = cover;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
