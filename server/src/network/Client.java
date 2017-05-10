package network;

import network.messaging.Message;
import network.messaging.distributor.balancer.BalancerDistributor;
import java.net.URL;

/**
 * Created by joao on 5/9/17.
 */
public class Client {

    public Client(){


        try {
            URL url = new URL("https://192.168.1.97:8000");
            Message.SendURLMessage(url, new Message(BalancerDistributor.REQUEST_SERVER, "Hello"), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] s){
        new Client();
    }


}
