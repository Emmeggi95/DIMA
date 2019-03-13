package com.dima.emmeggi95.jaycaves.me;

/**
 * Data objects to be stored in the real-time db related to artists
 */
public class Artist {

    private String name;
    private String birthdate;
    private String story;
    private String genre1;
    private String genre2;
    private String genre3;
    private String cover;

    public Artist() {
    }

    public Artist(String name, String birthdate, String story, String genre1, String cover) {
        this.name = name;
        this.birthdate = birthdate;
        this.story = story;
        this.genre1 = genre1;
        this.cover = cover;
    }

    public Artist(String name, String birthdate, String story, String genre1, String genre3, String cover) {
        this.name = name;
        this.birthdate = birthdate;
        this.story = story;
        this.genre1 = genre1;
        this.genre3 = genre3;
        this.cover = cover;
    }

    public Artist(String name, String birthdate, String story, String genre1, String genre2, String genre3, String cover) {
        this.name = name;
        this.birthdate = birthdate;
        this.story = story;
        this.genre1 = genre1;
        this.genre2 = genre2;
        this.genre3 = genre3;
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getStory() {
        return story;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public void setGenre1(String genre1) {
        this.genre1 = genre1;
    }

    public void setGenre2(String genre2) {
        this.genre2 = genre2;
    }

    public void setGenre3(String genre3) {
        this.genre3 = genre3;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "name='" + name + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", story='" + story + '\'' +
                ", genre1='" + genre1 + '\'' +
                ", genre2='" + genre2 + '\'' +
                ", genre3='" + genre3 + '\'' +
                ", cover='" + cover + '\'' +
                '}';
    }
}
