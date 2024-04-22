package com.example.quickcash.firebase;

import java.io.Serializable;

public class Chat implements Serializable {
    private String message;
    private String senderName;

    public Chat() {
        // Default constructor required for calls to DataSnapshot.getValue(Chat.class)
    }

    public Chat(String message, String senderName) {
        this.message = message;
        this.senderName = senderName;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}