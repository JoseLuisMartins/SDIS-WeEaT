package com.example.josemartins.sdis_weeat.gui;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import com.example.josemartins.sdis_weeat.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;




public class MapFragment extends Fragment implements GoogleMap.OnMapLongClickListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMarkerClickListener {

    private View rootView;
    private GoogleMap myMap;
    private MapView mMapView;
    private final Map<LatLng, Marker> mapMarkers = new ConcurrentHashMap<>();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        myMap = googleMap;
        myMap.setOnMapLongClickListener(this);

        myMap.setOnInfoWindowClickListener(marker -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            startActivity(intent);

        });

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        myMap.setMyLocationEnabled(true);

                        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();


                        LatLng currPoint = new LatLng(latitude, longitude);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(currPoint).zoom(12).build();
                        myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        addMarker(currPoint,"Current Position", "Este sitio é muito xiroo");

                    } else {
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                }

            }


        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.map_fragment, container, false);
        MapsInitializer.initialize(this.getActivity());
        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        return rootView;
    }


    private void addMarker(LatLng latLng, String title , String snippet){
        Marker marker = myMap.addMarker(new MarkerOptions().position(latLng).title(title).snippet(snippet));
        mapMarkers.put(latLng,marker);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        AlertDialog.Builder createGroup = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.create_group, null);

        EditText group_name = (EditText) v.findViewById(R.id.group_name);
        EditText group_date = (EditText) v.findViewById(R.id.group_date);

        group_date.setInputType(InputType.TYPE_NULL);
        group_date.setOnClickListener(v1 -> openTimePicker(group_date));

        createGroup.setView(v);

        createGroup.setTitle("Marcar encontro");

        createGroup.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        createGroup.setPositiveButton("Create", (dialog, id) -> {

            if(!group_name.getText().toString().trim().equals(""))
                addMarker(latLng, group_name.getText().toString(), group_date.getText().toString());
        });

        AlertDialog alertDialog = createGroup.create();

        alertDialog.show();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Intent i = new Intent(getActivity(),ChatActivity.class);
        startActivity(i);

        return true;
    }

    public void openTimePicker(EditText group_date){
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                (view, hourOfDay, minute) -> {
                    if(minute < 10)
                        group_date.setText(hourOfDay + ":0" + minute);
                    else
                        group_date.setText(hourOfDay + ":" + minute);
                }, mHour, mMinute, false);

        timePickerDialog.show();
    }

    public void changeMapType(int type){
        myMap.setMapType(type);
    }

}