package com.dima.emmeggi95.jaycaves.me.entities;

public class ChatPreview {

    private String email, username, lastMessage;
    private int unreadMessages;

    public ChatPreview(String email, String username, String lastMessage, int unreadMessages) {
        this.email = email;
        this.username = username;
        this.lastMessage = lastMessage;
        this.unreadMessages = unreadMessages;
    }

    public String getEmail() {
        return email;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setLastMessage(String lastMessage) {
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
}
