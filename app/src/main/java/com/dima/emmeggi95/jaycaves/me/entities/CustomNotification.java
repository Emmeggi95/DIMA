package com.dima.emmeggi95.jaycaves.me.entities;

import com.google.firebase.database.ServerValue;

public abstract class CustomNotification {

    protected String message, date;
    protected boolean read;
    private Object timestamp;

    public CustomNotification(String message, String date, boolean read) {
        this.message = message;
        this.date = date;
        this.read = read;
        timestamp = ServerValue.TIMESTAMP;
    }

    public CustomNotification(){}

    public void read(NotificationReader reader){}

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public boolean isRead() {
        return read;
    }


    public void setRead(boolean read){
        this.read = read;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
