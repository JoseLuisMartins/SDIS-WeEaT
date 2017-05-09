package com.example.josemartins.sdis_weeat.gui;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.josemartins.sdis_weeat.R;
import com.example.josemartins.sdis_weeat.logic.Utils;
import com.example.josemartins.sdis_weeat.network.Client;

public class ChooseLocal extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_local);


        Utils.client.makeRequest("AuthUser","POST","Body-> handle location".getBytes());

        android.app.FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        MapFragment mf =new MapFragment();
        ft.add(R.id.mapView,mf);
        ft.commit();


    }

    public void goToChat(View v){
        Intent i = new Intent(this,ChatActivity.class);
        startActivity(i);
    }

}
