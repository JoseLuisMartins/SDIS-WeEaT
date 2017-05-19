package network.messaging.distributor.client;

import android.app.Activity;
import android.util.Log;

import com.example.josemartins.sdis_weeat.gui.MapFragment;
import com.example.josemartins.sdis_weeat.logic.ActionObject;
import com.example.josemartins.sdis_weeat.logic.ChatArrayAdapter;
import com.example.josemartins.sdis_weeat.logic.ChatMessage;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import network.messaging.Message;
import network.messaging.distributor.Distributor;


public class ClientDistributor extends Distributor {

    public static final int RESPONSE = 0;
    public static final int UNLOGGED = 1;
    public static final int FILL_MAP_MARKERS = 2;
    public static final int FILL_MESSAGES = 3;

    public ClientDistributor(){

        addAction(RESPONSE, (Message m) -> response(m));
        addAction(UNLOGGED, (Message m) -> unLogged(m));
        addAction(FILL_MAP_MARKERS, (Message m) -> fillMapMarkers(m));
        addAction(FILL_MESSAGES, (Message m) -> fillMessages(m));

    }

    public void response(Message m){
        Log.d("debug",m.getContent().toString());
    }

    public void unLogged(Message m){
        Log.d("debug","Please Login Again");
    }


    public void fillMapMarkers(Message m) {
        //add the markers to the map


        try {
            Log.d("debug", "Map markers:\n " + m.getContent());
            ArrayList<Object> actionObjects =  m.getActionObjects();
            MapFragment mapFragment = (MapFragment) actionObjects.get(ActionObject.MAP_FRAGMENT);
            Activity mapActivity = (Activity) actionObjects.get(ActionObject.MAP_ACTIVITY);


            JSONObject markers = new JSONObject((String) m.getContent());
            JSONArray markersInf = markers.getJSONArray("chats");


            mapActivity.runOnUiThread(() -> {
                try {
                    for (int i = 0; i < markersInf.length(); i++) {
                        JSONObject marker = markersInf.getJSONObject(i);
                        LatLng pos = new LatLng(marker.getDouble("lat"),marker.getDouble("long"));
                        mapFragment.addMarker(pos,marker.getString("title"),Long.valueOf(marker.getLong("date")).toString());
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void fillMessages(Message m) {
        //fill the chat with the last messages

        try {
            Log.d("debug", "Chat messages:\n " + m.getContent());
            ArrayList<Object> actionObjects =  m.getActionObjects();
            ChatArrayAdapter chatAdapter= (ChatArrayAdapter) actionObjects.get(ActionObject.CHAT_ADAPTER);
            Activity chatActivity = (Activity) actionObjects.get(ActionObject.CHAT_ACTIVITY);


            JSONObject markers = new JSONObject((String) m.getContent());
            JSONArray markersInf = markers.getJSONArray("messages");


            chatActivity.runOnUiThread(() -> {
                try {
                    for (int i = 0; i < markersInf.length(); i++) {
                        JSONObject message= markersInf.getJSONObject(i);
                        chatAdapter.add(new ChatMessage(message));
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
