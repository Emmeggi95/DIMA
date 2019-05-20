package com.dima.emmeggi95.jaycaves.me.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserReference implements Serializable {
    private String username;
    private String email;
    private Bitmap cover;
    private List<Review> reviews;

    public UserReference(String username, String email, Bitmap cover, List<Review> reviews) {
        this.username = username;
        this.email = email;
        this.cover = cover;
        this.reviews = reviews;
    }

    public UserReference(String username, String email) {
        this.username = username;
        this.email = email;
        reviews = new ArrayList<>();
    }

    public UserReference() {
        // Get other information from DB
    }

    public String getUsername() {
        return username;
    }

    public Bitmap getCover() {
        return cover;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
