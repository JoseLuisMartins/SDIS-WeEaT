package com.example.josemartins.sdis_weeat.logic;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import network.messaging.Message;

public class ChatMessage {

    private String message;
    private MessageType messageType;
    private String name;
    private String image_url;
    private long date;

    public ChatMessage(String message, MessageType messageType, String name, String image_url){

        this.message = message;
        this.messageType = messageType;
        this.date = new Date().getTime();
        this.image_url = image_url;
        this.name = name;

    }



    public ChatMessage(JSONObject m){


        try {

            this.message = m.getString("content");
            this.image_url = m.getString("imageUrl");
            this.name = m.getString("name");
            this.date = new Date().getTime();

            //check if im the user who sent
            this.messageType = MessageType.SENT;



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    public String getMessage(){
        return message;
    }

    public MessageType getMessageType(){
        return messageType;
    }

    public String getName() {
        return name;
    }

    public long getDate() {
        return date;
    }

    @Override
    public String toString() {
        return message;
    }
}
