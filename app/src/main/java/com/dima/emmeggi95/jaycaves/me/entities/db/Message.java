package com.dima.emmeggi95.jaycaves.me.entities.db;

import com.google.firebase.database.ServerValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class Message {

    private String id, text, senderId, sender, chatId;
    private String time;
    private Object timestamp;

    public Message(String text, String time, String userId, String userName, String chatId) {
        this.text = text;
        this.time = time;
        this.senderId = userId;
        this.sender = userName;
        this.timestamp = ServerValue.TIMESTAMP;
        this.chatId=chatId;
    }

    public Message(){
        //
    }


    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getText() {
        return text;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSender() {
        return sender;
    }

    public String getTime() {
        return time;
    }

    public String getId() {
        return id;
    }

    public static Comparator<Message> dateComparator = new Comparator<Message>() {

        public int compare(Message m1, Message m2) {

            SimpleDateFormat formatter= new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date1 = new Date();
            Date date2 = new Date();
            try {
                date1 = formatter.parse(m1.getTime());
            } catch (ParseException e) {
                System.out.println("Error parsing date1");
            }
            try {
                date2 = formatter.parse(m2.getTime());
            } catch (ParseException e) {
                System.out.println("Error parsing date2");
            }
            return  date2.compareTo(date1);


        }};
}
