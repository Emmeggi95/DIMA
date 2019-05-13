package com.dima.emmeggi95.jaycaves.me;

public class UserPreference {

    private String username;
    private String coverphoto;

    public UserPreference(){

    }

    public UserPreference(String username, String coverphoto) {
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
