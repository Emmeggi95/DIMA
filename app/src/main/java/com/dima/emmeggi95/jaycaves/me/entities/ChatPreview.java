package com.dima.emmeggi95.jaycaves.me.entities;

public class ChatPreview {

    private String userId, lastMessage;
    private int unreadMessages;

    public ChatPreview(String userId, String lastMessage, int unreadMessages) {
        this.userId = userId;
        this.lastMessage = lastMessage;
        this.unreadMessages = unreadMessages;
    }

    public String getUserId() {
        return userId;
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
}
