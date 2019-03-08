package com.dima.emmegi95.jaycaves.sm2;

import android.net.Uri;

public class HomeAlbum {
    private String title;
    private String author;
    private String genre;
    private double score;
    private int coverSrc;

    public HomeAlbum(String title) {
        this.title = title;
    }

    public HomeAlbum(String title, String author, String genre, double score, int coverSrc) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.score = score;
        this.coverSrc = coverSrc;
    }

    public HomeAlbum(String title, String author, String genre, double score) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.score = score;

    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public double getScore() {
        return score;
    }

    public int getCover() {
        return coverSrc;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setCover(int cover) {
        this.coverSrc = cover;
    }
}
