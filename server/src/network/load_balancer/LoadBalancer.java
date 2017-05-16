package network.load_balancer;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsServer;
import network.ServerWeEat;
import network.messaging.Message;
import network.messaging.distributor.Distributor;
import network.messaging.distributor.balancer.BalancerDistributor;
import network.sockets.SecureServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

public class LoadBalancer implements HttpHandler {

    private HashMap<String, ServerPair> servers = new HashMap<>();
    private int port = 8000;
    private HttpsServer server;

    private Distributor distributor = new BalancerDistributor(this);


    public LoadBalancer(int port){
        try {
            new SecureServer(27015);


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

        LoadBalancer lb = new LoadBalancer(8000);

    }

    public int storeServer(String svLocation, String svIP, int svPort){

        if(servers.containsKey(svLocation))
            return servers.get(svLocation).AddServerConnection(new ServerConnection(svIP,svPort));

        ServerPair pair = new ServerPair();
        servers.put(svLocation, pair);
        return pair.AddServerConnection(new ServerConnection(svIP,svPort));
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
