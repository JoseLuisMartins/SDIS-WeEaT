package network;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


import java.io.IOException;
import java.io.OutputStream;

public class ServerHttpHandler implements HttpHandler {


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        byte [] response = "Do you already know WeEat?".getBytes();
        httpExchange.sendResponseHeaders(200, response.length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response);
        os.close();



    }
}
