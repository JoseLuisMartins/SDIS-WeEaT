package network;


import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;

@WebSocket
public class MyWebSocket {
    private RemoteEndpoint remote;

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("WebSocket Opened");
        this.remote = session.getRemote();
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        System.out.println("Message from Client: " + message);
        try {
            remote.sendString("Hi Client");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("WebSocket Closed. Code:" + statusCode);
    }
}