package com.example.josemartins.sdis_weeat.gui;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.josemartins.sdis_weeat.R;
import com.example.josemartins.sdis_weeat.logic.ChatArrayAdapter;
import com.example.josemartins.sdis_weeat.logic.ChatMessage;
import com.example.josemartins.sdis_weeat.logic.Utils;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import network.NotificationsWebSocket;
import network.messaging.Message;
import network.messaging.distributor.server.ServerDistributor;

public class ChatActivity extends AppCompatActivity {

    private TextView title;
    private TextView messageView;
    private ListView msgList;
    private ImageView sendBtn;
    private ChatArrayAdapter chatArrayAdapter;
    private LatLng chatId;
    private boolean side = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        title = (TextView) findViewById(R.id.title);
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






        //receive chat notifications
        Bundle res = getIntent().getExtras();
        chatId = new LatLng(res.getDouble("lat"),res.getDouble("long"));

        NotificationsWebSocket.request(chatArrayAdapter, this,chatId);

    }


    private boolean sendMessage(){

        if(!(messageView.getText().length() == 0)){

            try {

                JSONObject jsonAddMessage = new JSONObject();
                jsonAddMessage.put("content", messageView.getText().toString());
                jsonAddMessage.put("lat",chatId.latitude);
                jsonAddMessage.put("long",chatId.longitude);

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

    public void setTitle(String titleString){
        title.setText(titleString);
    }

}
