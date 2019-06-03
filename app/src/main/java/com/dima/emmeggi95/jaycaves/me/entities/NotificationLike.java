package com.dima.emmeggi95.jaycaves.me.entities;

public class NotificationLike extends Notification {

    String reviewId, reviewAlbum, userId, userName;

    public NotificationLike(String message, String date, boolean read, String reviewId, String reviewAlbum, String userId, String userName) {
        super(message, date, read);
        this.reviewId = reviewId;
        this.reviewAlbum = reviewAlbum;
        this.userId = userId;
        this.userName = userName;
        this.message = userName + " liked your review of " + reviewAlbum;
    }

    @Override
    public void read(NotificationReader reader) {
        reader.notify(this);
    }

    public String getReviewId() {
        return reviewId;
    }

    public String getReviewAlbum() {
        return reviewAlbum;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }
}
