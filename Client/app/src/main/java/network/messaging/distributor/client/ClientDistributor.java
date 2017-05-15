package network.messaging.distributor.client;

import android.util.Log;

import network.messaging.Message;
import network.messaging.distributor.Distributor;


public class ClientDistributor extends Distributor {

    public static final int RESPONSE = 0;
    public static final int UNLOGGED = 1;

    public ClientDistributor(){

        addAction(RESPONSE, (Message m) -> response(m));
        addAction(UNLOGGED, (Message m) -> unLogged(m));

    }

    public void response(Message m){
        Log.d("debug",m.getContent().toString());
    }

    public void unLogged(Message m){
        Log.d("debug","Please Login Again");
    }



}
