package com.dima.emmeggi95.jaycaves.me.entities.db;

import com.google.firebase.database.ServerValue;

import java.util.Date;

/**
 *
 */
public class ChatPreview implements Comparable<ChatPreview> {

    private String chatId,user_1, user_2;
    private Object lastMessage;
    private int unreadMessages_1;
    private int unreadMessages_2;

    public ChatPreview(){
        // For db purposes
    }


    public ChatPreview(String chatId, String user_1, String user_2, int unreadMessages_1, int unreadMessages_2) {
        this.chatId = chatId;
        this.user_1 = user_1;
        this.user_2 = user_2;
        this.lastMessage = ServerValue.TIMESTAMP;
        this.unreadMessages_1 = unreadMessages_1;
        this.unreadMessages_2= unreadMessages_2;
    }

  /* public String getLastMessageFormatted() {
        SimpleDateFormat std = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return std.format(lastMessage);
    } */


    public int getUnreadMessages_1() {
        return unreadMessages_1;
    }

    public void setUnreadMessages_1(int unreadMessages_1) {
        this.unreadMessages_1 = unreadMessages_1;
    }


    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUser_1() {
        return user_1;
    }

    public void setUser_1(String user_1) {
        this.user_1 = user_1;
    }

    public String getUser_2() {
        return user_2;
    }

    public void setUser_2(String user_2) {
        this.user_2 = user_2;
    }

    @Override
    public int compareTo(ChatPreview o) {
        return new Date((long)lastMessage).compareTo(new Date((long)o.getLastMessage()));
    }

    public Object getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Object lastMessage) {
        this.lastMessage = lastMessage;
    }
}
