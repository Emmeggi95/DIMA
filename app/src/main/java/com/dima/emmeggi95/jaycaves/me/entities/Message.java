package com.dima.emmeggi95.jaycaves.me.entities;

import java.text.DateFormat;

public class Message {

    private String text, userId, userName;
    private String time;

    public Message(String text, String time, String userId, String userName) {
        this.text = text;
        this.time = time;
        this.userId = userId;
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getTime() {
        return time;
    }
}
