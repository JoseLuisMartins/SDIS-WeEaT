package network.notification;


import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;


public class NotificationWebSocketClient {

    public static void main(String[] args) throws IOException, URISyntaxException {
        new NotificationWebSocketClient().run(new URI("ws://127.0.0.1:8080"));
    }

    public void run(URI destinationUri) throws IOException {

        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setTrustAll(true);
        sslContextFactory.setValidateCerts(false);

        /*Resource keyStoreResource = Resource.newResource(this.getClass().getResource("/truststore.jks"));
        sslContextFactory.setKeyStoreResource(keyStoreResource);
        sslContextFactory.setKeyStorePassword("password");
        sslContextFactory.setKeyManagerPassword("password");*/


        WebSocketClient client = new WebSocketClient(sslContextFactory);
        MyWebSocket socket = new MyWebSocket();

        try {
            client.start();

            ClientUpgradeRequest request = new ClientUpgradeRequest();
            System.out.println("Connecting to : " + destinationUri);
            client.connect(socket, destinationUri, request);
            socket.await();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                client.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @WebSocket
    public class MyWebSocket {
        private final CountDownLatch closeLatch = new CountDownLatch(1);

        @OnWebSocketConnect
        public void onConnect(Session session) {
            JSONObject obj = new JSONObject();
            obj.put("token","test");
            obj.put("chatId",1);

            System.out.println("WebSocket Opened in client side");
            try {
                System.out.println("Sending message: Hi server");
                session.getRemote().sendString(obj.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @OnWebSocketMessage
        public void onMessage(String message) {
            System.out.println("Message from Server: " + message);
        }

        @OnWebSocketClose
        public void onClose(int statusCode, String reason) {
            System.out.println("WebSocket Closed. Code:" + statusCode);
        }

        public void await() throws InterruptedException {
            this.closeLatch.await();
        }

    }
}
