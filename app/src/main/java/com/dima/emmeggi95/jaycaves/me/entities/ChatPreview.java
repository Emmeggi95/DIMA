package com.dima.emmeggi95.jaycaves.me.entities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatPreview implements Comparable<ChatPreview> {

    private String email, username;
    private long lastMessage;
    private int unreadMessages;

    public ChatPreview(String email, String username, long lastMessage, int unreadMessages) {
        this.email = email;
        this.username = username;
        this.lastMessage = lastMessage;
        this.unreadMessages = unreadMessages;
    }

    public String getEmail() {
        return email;
    }

    public String getLastMessageFormatted() {
        SimpleDateFormat std = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return std.format(lastMessage);
    }

    public long getLastMessage() {
        return lastMessage;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setLastMessage(long lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int compareTo(ChatPreview o) {
        return new Date(lastMessage).compareTo(new Date(o.getLastMessage()));
    }
}
