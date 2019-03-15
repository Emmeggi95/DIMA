package com.dima.emmeggi95.jaycaves.me.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeAlbum implements Serializable {
    private String title;
    private String author;
    private String genre;
    private String year;
    private double score;
    private int coverSrc;

    private List<Review> reviews = new ArrayList<Review>();

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

    public HomeAlbum(String title, String author, String genre, String year, double score, int coverSrc) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.year = year;
        this.score = score;
        this.coverSrc = coverSrc;
    }

    public HomeAlbum(String title, String author, String genre, String year, double score, int coverSrc, List<Review> reviews) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.year = year;
        this.score = score;
        this.coverSrc = coverSrc;
        this.reviews = reviews;
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

    public String getYear() {
        return year;
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

    public void setYear(String year) {
        this.year = year;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setCover(int cover) {
        this.coverSrc = cover;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
