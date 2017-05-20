package network.messaging.distributor.client;

import network.Client;
import network.messaging.Message;
import network.messaging.distributor.Distributor;

public class ClientDistributor extends Distributor{

    public static final int RESPONSE = 0;
    public static final int UNLOGGED = 1;
    public static final int FILL_MAP_MARKERS = 2;
    public static final int UPDATE_CHAT = 3;
    public static final int ADD_SERVER_LOCATIONS = 4;
    public static final int START_SERVER_CONNECTION = 5;

    private Client client;

    public ClientDistributor(Client client){
        this.client = client;
        addAction(RESPONSE, (Message m) -> fillChat(m));
    }

    public void fillChat(Message m){
       System.out.println(m.getContent().toString());
    }



}
