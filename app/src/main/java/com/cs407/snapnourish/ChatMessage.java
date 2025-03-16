package com.cs407.snapnourish;

public class ChatMessage {
    private String message;
    private boolean isUser;

    // Constructor
    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }
}
