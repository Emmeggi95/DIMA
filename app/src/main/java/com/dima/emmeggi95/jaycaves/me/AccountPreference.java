package com.dima.emmeggi95.jaycaves.me;

import android.graphics.Bitmap;

import com.dima.emmeggi95.jaycaves.me.entities.Review;

import java.io.Serializable;
import java.util.List;

/**
 * Contains username and cover photo reference of a certain user
 */
public class AccountPreference implements Serializable {

    private String username;
    private String coverphoto;

    public AccountPreference(){

    }

    public AccountPreference(String username, String coverphoto) {
        this.username = username;
        this.coverphoto = coverphoto;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCoverphoto() {
        return coverphoto;
    }

    public void setCoverphoto(String coverphoto) {
        this.coverphoto = coverphoto;
    }

}
