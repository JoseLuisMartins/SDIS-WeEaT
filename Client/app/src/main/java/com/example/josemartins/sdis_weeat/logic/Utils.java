package com.example.josemartins.sdis_weeat.logic;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.josemartins.sdis_weeat.R;
import com.example.josemartins.sdis_weeat.gui.ChatActivity;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import network.Client;

public class Utils {

    public static Client client;
    public static String loadBalancerUrl = "https://192.168.1.64:8000";
    public static String serverUrl;
    public static String webSocketUrl = "ws://192.168.1.64:8887";




    public static void createChatNotification(Activity activity, ChatMessage message){

        if(message.getMessageType() == MessageType.SENT)
            return;

        //create a notification
        Notification.Builder notification = new Notification.Builder(activity.getApplicationContext());
        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle(message.getChatTitle() + " " + Long.toString(message.getChatDate()));
        notification.setContentText(message.getName() + ": " + message.getMessage());

        Intent i = new Intent(activity.getApplicationContext(), ChatActivity.class);
        i.putExtra("lat",message.getLatitude());
        i.putExtra("long",message.getLongitude());
        i.putExtra("title",message.getChatTitle());
        i.putExtra("date",message.getChatDate());

        PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(),0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(55555,notification.build());
    }

}
