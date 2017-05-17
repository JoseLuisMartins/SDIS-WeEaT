package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;


public class JavaXWebSocket extends WebSocketServer {

    /** The web socket port number */
    private static int PORT = 8887;

    private Set<WebSocket> conns;
    private Map<WebSocket, String> nickNames;

    /**
     * Creates a new WebSocketServer with the wildcard IP accepting all connections.
     */
    public JavaXWebSocket() {
        super(new InetSocketAddress(PORT));
        conns = new HashSet<>();
        nickNames = new HashMap<>();
    }

    /**
     * Method handler when a new connection has been opened.
     */
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
        conns.add(conn);
    }

    /**
     * Method handler when a connection has been closed.
     */
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

        System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    /**
     * Method handler when a message has been received from the client.
     */
    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Received: " + message);
    }


    /**
     * Method handler when an error has occured.
     */
    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("ERROR from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }


    /**
     * Main method.
     */
    public static void main(String[] args) {
        JavaXWebSocket server = new JavaXWebSocket();
        server.start();
    }

}