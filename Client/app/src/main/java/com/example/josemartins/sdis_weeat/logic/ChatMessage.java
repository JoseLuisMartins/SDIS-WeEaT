package com.example.josemartins.sdis_weeat.logic;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


public class ChatMessage {

    private String message;
    private MessageType messageType;
    private String name;
    private String image_url;
    private String email;
    private String chatTitle;
    private Double latitude;
    private Double longitude;
    private long chatDate;



    public ChatMessage(String message, MessageType messageType, String name, String image_url, String email, String chatTitle, long chatdate, long date) {
        this.message = message;
        this.messageType = messageType;
        this.name = name;
        this.image_url = image_url;
        this.email = email;
        this.chatTitle = chatTitle;
        this.chatDate = chatdate;
        this.chatDate = date;
    }

    public ChatMessage(JSONObject m){


        try {

            this.message = m.getString("content");
            this.image_url = m.getString("imageUrl");
            this.name = m.getString("name");
            this.email = m.getString("poster");
            this.chatTitle = m.getString("title");
            this.chatDate = m.getLong("chatDate");
            this.latitude = m.getDouble("chat_lat");
            this.longitude = m.getDouble("chat_long");


            //check if i am the user who sent the message

            if(email.equals(Utils.client.getAccount().getEmail()))
                this.messageType = MessageType.SENT;
            else
                this.messageType = MessageType.RECEIVED;


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getChatTitle() {
        return chatTitle;
    }

    public long getChatDate() {
        return chatDate;
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


    public String getImage_url() {
        return image_url;
    }

    @Override
    public String toString() {
        return message;
    }
}
