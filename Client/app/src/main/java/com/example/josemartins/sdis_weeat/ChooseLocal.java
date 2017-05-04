package com.example.josemartins.sdis_weeat;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ChooseLocal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_local);

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
