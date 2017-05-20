package network.messaging.distributor.balancer;

import com.sun.net.httpserver.Headers;
import network.load_balancer.LoadBalancer;
import network.load_balancer.ServerConnection;
import network.messaging.Message;
import network.messaging.distributor.Distributor;
import network.messaging.distributor.client.ClientDistributor;
import network.messaging.distributor.server.ServerDistributor;
import network.sockets.ConnectionArmy;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by joao on 5/9/17.
 */
public class BalancerDistributor extends Distributor {

    public final static int REQUEST_SERVER = 0;
    public final static int REQUEST_LOCATIONS = 1;


    private LoadBalancer loadBalancer;

    public BalancerDistributor(LoadBalancer balancer){
        loadBalancer = balancer;
        addAction(REQUEST_SERVER, (Message m) -> requestServer(m));
        addAction(REQUEST_LOCATIONS , (Message m) -> requestLocations(m));
    }

    public void requestServer(Message m){

        String location = (String)m.getContent();

        JSONObject obj = new JSONObject();

        ServerConnection connection = loadBalancer.getServerConnectionByLocation(location);


        obj.put("ip", connection.getIP());
        obj.put("httpsPort",connection.getHttpsPort());
        obj.put("webPort", connection.getWebSocketPort());


        try {
           Distributor.sendMessage(m.getHttpExchange().getResponseBody(),
                   new Message(ClientDistributor.START_SERVER_CONNECTION, obj.toString()));

           System.out.println("Sent IP/Port" + obj.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void requestLocations(Message m){



        Collection<String> list = loadBalancer.getConnectionArmy().getLocations();

        JSONObject obj = new JSONObject();
        obj.put("locations", list);

        try{
            Distributor.sendMessage(m.getHttpExchange().getResponseBody(),
                    new Message((ClientDistributor.ADD_SERVER_LOCATIONS),obj.toString()));
            System.out.println("Sent ServerLocations");
        }catch(Exception e){
            e.printStackTrace();
        }

    }


}
