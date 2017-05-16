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

public class ChatActivity extends AppCompatActivity {

    private TextView title;
    private TextView message;
    private ListView msgList;
    private ImageView sendBtn;
    private ChatArrayAdapter chatArrayAdapter;
    private boolean side = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        title = (TextView) findViewById(R.id.title);
        message = (TextView) findViewById(R.id.message);
        msgList = (ListView) findViewById(R.id.msgList);
        sendBtn = (ImageView) findViewById(R.id.sendButton);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(),R.layout.left_message);
        msgList.setAdapter(chatArrayAdapter);

        message.setOnKeyListener((View v, int keyCode, KeyEvent event) -> {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendMessage();
                }
                return false;
            }
        );

        sendBtn.setOnClickListener((View arg0) -> sendMessage());

        msgList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgList.setAdapter(chatArrayAdapter);


        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                msgList.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
    }


    private boolean sendMessage(){

        if(!(message.getText().length() == 0)){

            //send  message
            //client.makeRequest("AuthUser", "POST", message.toString().getBytes());

            chatArrayAdapter.add(new ChatMessage(message.getText().toString(), side, null));
            message.setText("");
            return true;
        }else{
            return false;
        }

    }

    public void setTitle(String _title){
        title.setText(_title);
    }

}
