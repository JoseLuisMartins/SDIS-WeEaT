package network;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import network.messaging.Message;
import network.messaging.distributor.Distributor;
import network.messaging.distributor.client.ClientDistributor;
import network.messaging.distributor.server.ServerDistributor;
import org.json.JSONObject;


import java.io.*;
import java.net.URI;
import java.util.Map;

import static network.GoogleLoginChecker.googleLoginChecker;

public class ServerHttpHandler implements HttpHandler {
    Distributor dist;

    public ServerHttpHandler(Server server) {
        dist = new ServerDistributor(server);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        ObjectInputStream in = new ObjectInputStream(httpExchange.getRequestBody());
        System.out.println("Received request");
        httpExchange.sendResponseHeaders(200, 0);

        try {

            JSONObject userInfo = googleLoginChecker(httpExchange.getRequestHeaders().getFirst("token"));

            if(userInfo == null) {
                System.out.println("User not loged in");
                Distributor.sendMessage(httpExchange.getResponseBody(), new Message(ClientDistributor.UNLOGGED, "mequie"));
                return;
            }

            Message m = (Message)in.readObject();
            System.out.println(m.getContent().toString());
            in.close();
            m.setUserInfo(userInfo);
            m.setHttpExchange(httpExchange);
            dist.distribute(m);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
