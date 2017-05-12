package network.messaging.distributor.client;

import network.Client;
import network.Server;
import network.messaging.Message;
import network.messaging.distributor.Distributor;

public class ClientDistributor extends Distributor{

    public static final int RESPONSE = 0;

    private Client client;

    public ClientDistributor(Client client){
        this.client = client;
        addAction(RESPONSE, (Message m) -> fillChat(m));

    }

    public void fillChat(Message m){
       System.out.println(m.getContent().toString());
    }



}
