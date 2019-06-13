package com.dima.emmeggi95.jaycaves.me.entities;

import com.dima.emmeggi95.jaycaves.me.entities.db.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

public class NotificationLike extends CustomNotification {

    String reviewId, reviewAlbum, reviewAuthor, reviewAuthorEmail, liker;

    public NotificationLike(String message, String date, boolean read, String reviewId, String reviewAlbum, String reviewAuthor, String liker) {
        super(message, date, read);
        this.reviewId = reviewId;
        this.reviewAlbum = reviewAlbum;
        this.reviewAuthor = reviewAuthor;
        this.liker = liker;
    }

    public NotificationLike(){
        super();
    }

    @Override
    public void read(NotificationReader reader) {
        reader.notify(this);
    }

    public static Comparator<NotificationLike> dateComparator = new Comparator<NotificationLike>() {

        public int compare(NotificationLike n1, NotificationLike n2) {

            SimpleDateFormat formatter= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date1 = new Date();
            Date date2 = new Date();
            try {
                date1 = formatter.parse(n1.getDate());
            } catch (ParseException e) {
                System.out.println("Error parsing date1");
            }
            try {
                date2 = formatter.parse(n2.getDate());
            } catch (ParseException e) {
                System.out.println("Error parsing date2");
            }
            return  date2.compareTo(date1);




        }};

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getReviewAlbum() {
        return reviewAlbum;
    }

    public void setReviewAlbum(String reviewAlbum) {
        this.reviewAlbum = reviewAlbum;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public void setReviewAuthor(String reviewAuthor) {
        this.reviewAuthor = reviewAuthor;
    }

    public String getLiker() {
        return liker;
    }

    public void setLiker(String liker) {
        this.liker = liker;
    }


}
