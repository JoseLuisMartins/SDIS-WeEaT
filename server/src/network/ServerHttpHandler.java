package network;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class ServerHttpHandler implements HttpHandler {


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("handle request");

        String method = httpExchange.getRequestMethod();
        URI uri = httpExchange.getRequestURI();

        System.out.println("method-> " + method + " uri-> " + uri);


        byte [] response = "Do you already know WeEat?".getBytes();
        httpExchange.sendResponseHeaders(200, response.length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response);
        os.close();


    }
}
