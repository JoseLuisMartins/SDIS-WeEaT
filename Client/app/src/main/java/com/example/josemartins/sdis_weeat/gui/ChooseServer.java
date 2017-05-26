package com.example.josemartins.sdis_weeat.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.TextView;

import com.example.josemartins.sdis_weeat.R;
import com.example.josemartins.sdis_weeat.logic.ActionObject;
import com.example.josemartins.sdis_weeat.logic.Utils;



import java.io.IOException;
import java.util.ArrayList;

import network.messaging.Message;
import network.messaging.distributor.balancer.BalancerDistributor;

public class ChooseServer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_server);

        Utils.context = this;

        ListView serverListView = (ListView) findViewById(R.id.serverList);

        ArrayList<Object> actionObject = new ArrayList<>();
        actionObject.add(ActionObject.SERVER_LIST_VIEW,serverListView);
        actionObject.add(ActionObject.SERVER_ACTIVITY,this);

        Utils.client.setActionObjects(actionObject);

        try {
            Utils.client.makeRequest(Utils.loadBalancerUrl,"POST",new Message(BalancerDistributor.REQUEST_LOCATIONS, new String()));
        } catch (IOException e) {
            e.printStackTrace();
        }



        serverListView.setOnItemClickListener((AdapterView<?> parent, View viewClicked, int position, long id) -> {
                    String chosenServer = ((TextView) viewClicked).getText().toString();
                    Log.d("debug",chosenServer);

                    try {

                        Utils.client.makeRequest(Utils.loadBalancerUrl,"POST",new Message(BalancerDistributor.REQUEST_SERVER, chosenServer));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
        });
    }



}
