package network;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import network.messaging.Message;
import network.messaging.ServerMessageParser;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public class ServerHttpHandler implements HttpHandler {
    ServerMessageParser messageParser;

    public ServerHttpHandler() {
        messageParser = new ServerMessageParser();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("handle request");

        String method = httpExchange.getRequestMethod();
        URI uri = httpExchange.getRequestURI();

        String workerId = uri.getPath().replaceFirst("/","");
        System.out.println("method-> " + method + " workerId-> " + workerId);

        //get request body
        InputStream is = httpExchange.getRequestBody();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1) {
            buffer.write(b);
        }
        buffer.flush();
        byte[] body = buffer.toByteArray();
        System.out.println("Body-> " + new String(body));


        //Check authentication here
        
        messageParser.ReceiveMessage(new Message(workerId,body,httpExchange));
    }


    public static void sendResponse(HttpExchange httpExchange, int code, String res) throws IOException {
        byte [] response = res.getBytes();
        httpExchange.sendResponseHeaders(code, response.length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response);
        os.close();
    }
}
