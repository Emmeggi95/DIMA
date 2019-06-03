package com.dima.emmeggi95.jaycaves.me.entities;

public abstract class Notification {

    protected String message, date;
    protected boolean read;

    public Notification(String message, String date, boolean read) {
        this.message = message;
        this.date = date;
        this.read = read;
    }

    public void read(NotificationReader reader){}

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(){
        read = true;
    }
}
