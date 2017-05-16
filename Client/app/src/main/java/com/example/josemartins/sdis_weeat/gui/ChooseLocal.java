package com.example.josemartins.sdis_weeat.gui;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.josemartins.sdis_weeat.R;

public class ChooseLocal extends AppCompatActivity {

    private Spinner time;
    private TextView choseLocalTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_local);



        time = (Spinner) findViewById(R.id.time);

        //Utils.client.makeRequest("AuthUser","POST","Body-> handle location".getBytes());

        android.app.FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        MapFragment mf =new MapFragment();


        ft.add(R.id.map,mf);
        ft.commit();

    }

    public void goToChat(View v){
        Intent i = new Intent(this,ChatActivity.class);
        startActivity(i);
    }

}
