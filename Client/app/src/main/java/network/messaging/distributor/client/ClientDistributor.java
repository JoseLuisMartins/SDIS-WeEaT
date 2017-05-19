package network.messaging.distributor.client;

import android.util.Log;

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

        Log.d("debug", "Map markers:\n " + m.getContent());


    }

    public void fillMessages(Message m) {
        //fill the chat with the last messages


    }

}
