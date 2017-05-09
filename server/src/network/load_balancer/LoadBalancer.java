package network.load_balancer;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsServer;
import network.Server;
import network.messaging.Message;
import network.messaging.MessageParser;
import network.messaging.distributor.balancer.BalancerDistributor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class LoadBalancer implements HttpHandler {

    private HashMap<String,String> nodes;
    private int port = 8000;
    private HttpsServer server;

    private MessageParser parser = new MessageParser(new BalancerDistributor(this));


    public LoadBalancer(int port){
        this.port = port;
        try {
            server = Server.getHttpsServer(port);
            server.createContext("/", (HttpHandler)this);


        } catch (Exception e) {
            e.printStackTrace();
        }

        server.start();

    }

    public static void main(String[] r){

        LoadBalancer lb = new LoadBalancer(8000);


    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        Map<String, Object > attributes =  httpExchange.getHttpContext().getAttributes();

        ObjectInputStream in = new ObjectInputStream(httpExchange.getRequestBody());

        httpExchange.sendResponseHeaders(201,0);

        try {
            Message m = (Message)in.readObject();
            m.setHttpExchange(httpExchange);
            parser.ReceiveMessage(m);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
