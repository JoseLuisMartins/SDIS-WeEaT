package com.example.josemartins.sdis_weeat.gui;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.josemartins.sdis_weeat.R;
import com.google.android.gms.maps.GoogleMap;

public class ChooseLocal extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_local);

        android.app.FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        MapFragment mf =new MapFragment();

        setTitle("Choose a place to eat");


        ImageButton btTerrain = (ImageButton) findViewById(R.id.terrain);
        ImageButton btSatellite = (ImageButton) findViewById(R.id.satellite);
        ImageButton btNormal = (ImageButton) findViewById(R.id.normal);


        btTerrain.setOnClickListener(v -> mf.changeMapType(GoogleMap.MAP_TYPE_TERRAIN));
        btSatellite.setOnClickListener(v -> mf.changeMapType(GoogleMap.MAP_TYPE_SATELLITE));
        btNormal.setOnClickListener(v -> mf.changeMapType(GoogleMap.MAP_TYPE_NORMAL));

        ft.add(R.id.map,mf);
        ft.commit();
    }


}
