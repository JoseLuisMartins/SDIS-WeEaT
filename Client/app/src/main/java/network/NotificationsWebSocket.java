package network;


import android.util.Log;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class NotificationsWebSocket extends WebSocketListener {

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        webSocket.send("hi");

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
        
        Request request = new Request.Builder().url("ws://192.168.1.64:8887").build();
        NotificationsWebSocket listener = new NotificationsWebSocket();
        WebSocket ws = client.newWebSocket(request,listener);

        client.dispatcher().executorService().shutdown();
    }
}

