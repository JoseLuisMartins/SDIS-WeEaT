package network.messaging.distributor.client;

import android.app.Activity;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.SubMenu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.josemartins.sdis_weeat.R;
import com.example.josemartins.sdis_weeat.gui.ChooseLocal;
import com.example.josemartins.sdis_weeat.gui.MapFragment;
import com.example.josemartins.sdis_weeat.logic.ActionObject;
import com.example.josemartins.sdis_weeat.logic.ChatArrayAdapter;
import com.example.josemartins.sdis_weeat.logic.ChatMessage;
import com.example.josemartins.sdis_weeat.logic.Utils;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import network.messaging.Message;
import network.messaging.distributor.Distributor;
import network.messaging.distributor.server.ServerDistributor;


public class ClientDistributor extends Distributor {

    public static final int RESPONSE = 0;
    public static final int UNLOGGED = 1;
    public static final int FILL_MAP_MARKERS = 2;
    public static final int UPDATE_CHAT = 3;
    public static final int ADD_SERVER_LOCATIONS = 4;
    public static final int START_SERVER_CONNECTION = 5;

    public ClientDistributor(){

        addAction(RESPONSE, (Message m) -> response(m));
        addAction(UNLOGGED, (Message m) -> unLogged(m));
        addAction(FILL_MAP_MARKERS, (Message m) -> fillMapMarkers(m));
        addAction(UPDATE_CHAT, (Message m) -> updateChat(m));
        addAction(ADD_SERVER_LOCATIONS, (Message m) -> addServerLocations(m));
        addAction(START_SERVER_CONNECTION, (Message m) -> startServerConnection(m));

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

                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(Long.valueOf(marker.getLong("date")));
                        String date = DateFormat.format("HH:mm", cal).toString();

                        mapFragment.addMarker(pos,marker.getString("title"),date);
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void updateChat(Message m) {
        //fill the chat with the last messages

        try {
            Log.d("debug", "Chat messageees:\n " + m.getContent());
            ArrayList<Object> actionObjects =  m.getActionObjects();
            ChatArrayAdapter chatAdapter= (ChatArrayAdapter) actionObjects.get(ActionObject.CHAT_ADAPTER);
            Activity chatActivity = (Activity) actionObjects.get(ActionObject.CHAT_ACTIVITY);
            Menu menu = (Menu) actionObjects.get(ActionObject.CHAT_MENU);


            JSONObject messages = new JSONObject((String) m.getContent());
            JSONArray messagesInf = messages.getJSONArray("messages");
            JSONArray chatMembersInf = messages.getJSONArray("chatMembers");


            chatActivity.runOnUiThread(() -> {
                try {
                    menu.clear();
                    chatAdapter.clear();

                    SubMenu s = menu.addSubMenu("Chat Participants");
                    for (int i = 0; i < chatMembersInf.length(); i++) {
                        JSONObject chatMember= chatMembersInf.getJSONObject(i);
                        s.add((String)chatMember.get("name"));

                    }

                    for (int i = 0; i < messagesInf.length(); i++) {
                        JSONObject message= messagesInf.getJSONObject(i);
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

    public void addServerLocations(Message m){
        //update listview
        Log.d("debug", "Server Locations:\n " + m.getContent());
        ArrayList<Object> actionObjects =  m.getActionObjects();
        ListView serverListView= (ListView) actionObjects.get(ActionObject.SERVER_LIST_VIEW);
        Activity serverActivity= (Activity) actionObjects.get(ActionObject.SERVER_ACTIVITY);

        try {
            JSONObject locations = new JSONObject((String) m.getContent());

            Log.d("debug",m.getContent().toString());
            JSONArray locationsJson =locations.getJSONArray("locations");

            String[] serverLocations = new String[locationsJson.length()];

            for(int i=0; i < locationsJson.length(); i++)
                serverLocations[i] = locationsJson.getString(i);

            Log.d("debug",serverLocations.toString());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(serverActivity, R.layout.server_list_element,serverLocations);

            serverActivity.runOnUiThread(() -> {
                serverListView.setAdapter(adapter);
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void startServerConnection(Message m)  {
        try {
            Log.d("debug", "Server Info:\n " + m.getContent());
            ArrayList<Object> actionObjects =  m.getActionObjects();
            Activity serverActivity= (Activity) actionObjects.get(ActionObject.SERVER_ACTIVITY);

            //Get the Server Url
            JSONObject serverInf = new JSONObject((String) m.getContent());

            StringBuilder sbHttps = new StringBuilder();
            sbHttps.append("https://");
            sbHttps.append(serverInf.getString("ip"));
            sbHttps.append(":");
            sbHttps.append(serverInf.getString("httpsPort"));

            Utils.serverUrl=sbHttps.toString();

            StringBuilder sbWs = new StringBuilder();
            sbWs.append("ws://");
            sbWs.append(serverInf.getString("ip"));
            sbWs.append(":");
            sbWs.append(serverInf.getString("webPort"));

            Utils.webSocketUrl = sbWs.toString();
            Log.d("debug","webSocket" + Utils.webSocketUrl);


            //Add user
            JSONObject jsonUser = new JSONObject();
            jsonUser.put("token", Utils.client.getToken());
            Utils.client.makeRequest(Utils.serverUrl,"POST",new Message(ServerDistributor.ADD_USER, jsonUser.toString()));

            //Go to choose local activity
            Intent i = new Intent(serverActivity, ChooseLocal.class);
            serverActivity.startActivity(i);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
