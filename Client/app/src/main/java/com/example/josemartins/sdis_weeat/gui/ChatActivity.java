package com.example.josemartins.sdis_weeat.gui;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.josemartins.sdis_weeat.R;
import com.example.josemartins.sdis_weeat.logic.ActionObject;
import com.example.josemartins.sdis_weeat.logic.ChatArrayAdapter;
import com.example.josemartins.sdis_weeat.logic.Utils;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import network.NotificationsWebSocket;
import network.messaging.Message;
import network.messaging.distributor.server.ServerDistributor;

public class ChatActivity extends AppCompatActivity {

    private TextView messageView;
    private ListView msgList;
    private ImageView sendBtn;
    private ChatArrayAdapter chatArrayAdapter;
    private LatLng chatId;
    private boolean side = true;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Utils.context = this;
        messageView = (TextView) findViewById(R.id.message);
        msgList = (ListView) findViewById(R.id.msgList);
        sendBtn = (ImageView) findViewById(R.id.sendButton);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(),R.layout.left_message);
        msgList.setAdapter(chatArrayAdapter);
        msgList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);


        messageView.setOnKeyListener((View v, int keyCode, KeyEvent event) -> {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendMessage();
                }
                return false;
            }
        );

        sendBtn.setOnClickListener((View arg0) -> sendMessage());


        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                msgList.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        //side bar
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        navView = (NavigationView) findViewById(R.id.navigationView);


        //receive chat notifications
        Bundle res = getIntent().getExtras();
        chatId = new LatLng(res.getDouble("lat"),res.getDouble("long"));
        setTitle(res.getString("title") + "\n" + res.getString("date"));

        NotificationsWebSocket.request(chatArrayAdapter, this,chatId,navView.getMenu());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(drawerToggle.onOptionsItemSelected(item))
            return true;


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {

            ArrayList<Object> actionObject = new ArrayList<>();
            actionObject.add(ActionObject.CHAT_ADAPTER,chatArrayAdapter);
            actionObject.add(ActionObject.CHAT_ACTIVITY,this);
            actionObject.add(ActionObject.CHAT_MENU,navView.getMenu());
            Utils.client.setActionObjects(actionObject);

            JSONObject jsonGetChatMessages = new JSONObject();
            jsonGetChatMessages.put("lat",chatId.latitude);
            jsonGetChatMessages.put("long",chatId.longitude);

            Utils.client.makeRequest(Utils.serverUrl,"POST",new Message(ServerDistributor.GET_CHAT_DATA, jsonGetChatMessages.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean sendMessage(){

        if(!(messageView.getText().length() == 0)){

            try {
                Calendar c = Calendar.getInstance();
                long date = c.getTimeInMillis();

                JSONObject jsonAddMessage = new JSONObject();
                jsonAddMessage.put("content", messageView.getText().toString());
                jsonAddMessage.put("lat",chatId.latitude);
                jsonAddMessage.put("long",chatId.longitude);
                jsonAddMessage.put("date",date);

                Utils.client.makeRequest(Utils.serverUrl,"POST",new Message(ServerDistributor.ADD_CHAT_MESSAGE, jsonAddMessage.toString()));

                messageView.setText("");

            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }else{
            return false;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(Utils.context,ChooseLocal.class);
        Utils.context.startActivity(i);
    }
}
