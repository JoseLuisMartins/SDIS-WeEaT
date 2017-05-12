package network;

import network.messaging.Message;
import network.messaging.distributor.Distributor;
import network.messaging.distributor.balancer.BalancerDistributor;
import network.messaging.distributor.client.ClientDistributor;
import network.messaging.distributor.server.ServerDistributor;
import org.json.JSONObject;

import java.net.URL;


public class Client {

    public Client(){

        try {
            URL url = new URL("https://192.168.1.64:8000");
            Distributor d = new ClientDistributor(this);
            JSONObject jsonTest = new JSONObject();
            jsonTest.put("lat",2.111);
            jsonTest.put("long",4.555);
            jsonTest.put("timestamp",2546);


            Message.SendURLMessage(url, new Message(ServerDistributor.ADD_CHAT_GROUP, jsonTest.toString()), d);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] s){
        new Client();
    }


}
