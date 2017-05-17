package network;


import android.os.AsyncTask;


import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

public class NotificationsWebSocket extends AsyncTask {


    @Override
    protected Object doInBackground(Object[] params) {
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


            URI uri = new URI("ws://192.168.1.64:8887");

            ClientUpgradeRequest request = new ClientUpgradeRequest();
            System.out.println("Connecting to : " + uri);
            client.connect(socket, uri, request);
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


        return null;
    }



    @WebSocket
    public class MyWebSocket {
        private final CountDownLatch closeLatch = new CountDownLatch(1);

        @OnWebSocketConnect
        public void onConnect(Session session) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("chatId",1);
                obj.put("token","test");

                System.out.println("WebSocket Opened in client side");

                System.out.println("Sending message: Hi server");
                session.getRemote().sendString(obj.toString());
            } catch (Exception e) {
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





















/*
import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class NotificationsWebSocket extends WebSocketListener {

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        webSocket.send("hi");
        webSocket.close(1000,"ADIOS");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d("Debug","message: " + text);
    }


    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000,"ADIOS");
        Log.d("Debug","close: " + code + " " + " reason: " + reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
    }

    public static void request(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("ws://127.0.0.1:8887").build();
        NotificationsWebSocket listener = new NotificationsWebSocket();
        WebSocket ws = client.newWebSocket(request,listener);

        client.dispatcher().executorService().shutdown();
    }
}*/