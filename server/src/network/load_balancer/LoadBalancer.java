package network.load_balancer;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsServer;
import network.ServerWeEat;
import network.messaging.Message;
import network.messaging.distributor.Distributor;
import network.messaging.distributor.balancer.BalancerDistributor;
import network.sockets.ConnectionArmy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class LoadBalancer implements HttpHandler {


    private int port = 8000;
    private HttpsServer server;
    private ConnectionArmy army;
    private Distributor distributor = new BalancerDistributor(this);


    public LoadBalancer(int port, int armyPort, int armyNumber){
        try {

            army = new ConnectionArmy(armyPort, armyNumber);


        } catch (Exception e) {
            e.printStackTrace();
        }
        this.port = port;
        try {
            server = ServerWeEat.getHttpsServer(port);
            server.createContext("/", this);

        } catch (Exception e) {
            e.printStackTrace();
        }

        server.start();

    }

    public static void main(String[] r){

        LoadBalancer lb = new LoadBalancer(8000, 27015,3);

    }

    public ConnectionArmy getConnectionArmy(){
        return army;
    }

    public ServerConnection getServerConnectionByLocation(String location){
        ConcurrentHashMap<String, ServerPair> servers = army.getServers();

        if(!servers.containsKey(location))
            return null;

        return  servers.get(location).getOperatingServer();
    }


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        ObjectInputStream in = new ObjectInputStream(httpExchange.getRequestBody());

        httpExchange.sendResponseHeaders(200, 0);

        try {
            Message m = (Message)in.readObject();
            in.close();
            m.setHttpExchange(httpExchange);
            distributor.distribute(m);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
