package network;

import network.messaging.Message;
import network.messaging.distributor.Distributor;
import network.messaging.distributor.balancer.BalancerDistributor;
import network.messaging.distributor.client.ClientDistributor;
import network.messaging.distributor.server.ServerDistributor;

import java.net.URL;

/**
 * Created by joao on 5/9/17.
 */
public class Client {

    public Client(){

        try {
            URL url = new URL("https://192.168.1.64:8000");
            Distributor d = new ClientDistributor(this);
            Message.SendURLMessage(url, new Message(ServerDistributor.ADD_USER, "Hello req chat"), d);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] s){
        new Client();
    }


}
