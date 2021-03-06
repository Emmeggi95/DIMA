package com.dima.emmeggi95.jaycaves.me.entities.db;

import com.google.firebase.database.ServerValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Data objects to be stored in the real-time db related to albums
 */
public class Album implements Serializable {


    private String title;
    private String date;
    private double score;
    private String artist;
    private String genre1;
    private String genre2;
    private String genre3;
    private String cover;
    private Object creation;
    private Object reviewed;

    private List<Review> reviews = new ArrayList<>();


    public Album(){

    }

    public Album(String title, String date, String artist, String genre1, String genre2, String genre3, String cover) {
        this.title = title;
        this.date = date;
        this.artist = artist;
        this.genre1 = genre1;
        this.genre2 = genre2;
        this.genre3 = genre3;
        this.cover = cover;
        this.score=0.0;
        this.creation= ServerValue.TIMESTAMP;


    }

    public Album(String title, String date, String artist, String genre1, String cover) {
        this.title = title;
        this.date = date;
        this.artist = artist;
        this.genre1 = genre1;
        this.cover = cover;
        this.score= 0.0;
        this.creation = ServerValue.TIMESTAMP;


    }

    public Album(String title, String date, String artist, String genre1, String genre2, String cover) {
        this.title = title;
        this.date = date;
        this.artist = artist;
        this.genre1 = genre1;
        this.genre2 = genre2;
        this.cover = cover;
        this.score=0.0;
        this.creation= ServerValue.TIMESTAMP;


    }

    public static Comparator<Album> scoreComparator = new Comparator<Album>() {

        public int compare(Album a1, Album a2) {

            Double score1 = a1.getScore();
            Double score2= a2.getScore();

            return  score2.compareTo(score1);


        }};




    public Album(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public double getScore() {
        return score;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre1() {
        return genre1;
    }

    public String getGenre2() {
        return genre2;
    }

    public String getGenre3() {
        return genre3;
    }

    public String getCover() {
        return cover;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setGenre1(String genre1) {
        this.genre1 = genre1;
    }

    public void setGenre2(String genre2) {
        this.genre2 = genre2;
    }

    public void setGenre3(String genre3) { this.genre3 = genre3; }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Object getCreation(){
        return creation;
    }

    public Object getReviewed() {
        return reviewed;
    }


    @Override
    public String toString() {
        return "{" + title +
                ", by " + artist +
                '}';
    }
}
