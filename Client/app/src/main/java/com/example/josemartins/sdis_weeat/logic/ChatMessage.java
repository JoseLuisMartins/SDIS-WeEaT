package com.example.josemartins.sdis_weeat.logic;


import java.util.Date;

public class ChatMessage {

    private String message;
    private boolean left;
    private String user;
    private long date;

    public ChatMessage(String message, boolean left, String user){
        super();
        this.message = message;
        this.left = left;
        this.date = new Date().getTime();
    }

    public String getMessage(){
        return message;
    }

    public boolean getLeft(){
        return left;
    }

    public String getUser() {
        return user;
    }

    public long getDate() {
        return date;
    }

    @Override
    public String toString() {
        return message;
    }
}
