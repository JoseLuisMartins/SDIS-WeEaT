package network.messaging.distributor.balancer;

import com.sun.net.httpserver.Headers;
import network.load_balancer.LoadBalancer;
import network.messaging.Message;
import network.messaging.distributor.Distributor;
import network.messaging.distributor.server.ServerDistributor;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by joao on 5/9/17.
 */
public class BalancerDistributor extends Distributor {

    public final static int REQUEST_SERVER = 0;
    public final static int STORE_SERVER = 1;


    private LoadBalancer loadBalancer;

    public BalancerDistributor(LoadBalancer balancer){
        loadBalancer = balancer;
        addAction(REQUEST_SERVER, (Message m) -> requestServer(m));
        addAction(STORE_SERVER  , (Message m) -> storeServer(m));
    }

    public void requestServer(Message m){

        System.out.println((String)m.getContent());

        try {
           Distributor.sendMessage(m.getHttpExchange().getResponseBody(), new Message(ServerDistributor.SET_MODE,"HelloMan", null));
           System.out.println("Message Sent");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void storeServer(Message m){
        JSONObject obj = new JSONObject((String)m.getContent());
        String ip = "";
        System.out.println(m.getHttpExchange().getRequestHeaders().toString());
        Headers header = m.getHttpExchange().getRequestHeaders();
        for(String e : header.keySet()){

            System.out.println(e + " - " + header.get(e));

        }


        int res = loadBalancer.storeServer(obj.getString("location"), ip , obj.getInt("port"));
        try {
            Distributor.sendMessage(m.getHttpExchange().getResponseBody(), new Message(ServerDistributor.SET_MODE, res));


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
