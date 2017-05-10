package network;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import network.messaging.Message;
import network.messaging.distributor.Distributor;
import network.messaging.distributor.server.ServerDistributor;


import java.io.*;
import java.net.URI;
import java.util.Map;

public class ServerHttpHandler implements HttpHandler {
    Distributor dist;



    public ServerHttpHandler(Server server) {
        dist = new ServerDistributor(server);


    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Map<String, Object > attributes =  httpExchange.getHttpContext().getAttributes();

        ObjectInputStream in = new ObjectInputStream(httpExchange.getRequestBody());

        httpExchange.sendResponseHeaders(200, 0);

        try {
            Message m = (Message)in.readObject();
            in.close();
            m.setHttpExchange(httpExchange);
            dist.distribute(m);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void sendResponse(HttpExchange httpExchange, int code, String res) throws IOException {
        byte [] response = res.getBytes();
        httpExchange.sendResponseHeaders(code, response.length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response);
        os.close();
    }
}
