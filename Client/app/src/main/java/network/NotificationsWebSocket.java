package network;


import android.app.Activity;
import android.util.Log;


import com.example.josemartins.sdis_weeat.logic.ChatArrayAdapter;
import com.example.josemartins.sdis_weeat.logic.ChatMessage;
import com.example.josemartins.sdis_weeat.logic.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class NotificationsWebSocket extends WebSocketListener {
    private Activity activity;
    private ChatArrayAdapter chatArrayAdapter;
    private int chatId;

    @Override
    public void onOpen(WebSocket webSocket, Response response) {

        try {
            JSONObject obj = new JSONObject();
            obj.put("chatId",chatId);
            obj.put("token", Utils.client.getToken());
            webSocket.send(obj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d("Debug","message: " + text);



        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject message= new JSONObject(text);
                    chatArrayAdapter.add(new ChatMessage(message));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }


    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        Log.d("Debug","close: " + code + " " + " reason: " + reason);
        //webSocket.close(1000,"ADIOS");
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.d("Debug","close: " + code + " " + " reason: " + reason);
        //super.onClosed(webSocket, code, reason);
    }


    public NotificationsWebSocket(ChatArrayAdapter chatArrayAdapter,Activity activity ,int chatId) {
        this.chatArrayAdapter = chatArrayAdapter;
        this.activity = activity;
        this.chatId = chatId;
    }

    public static  void request(ChatArrayAdapter chatArrayAdapter , Activity activity ,int chatId){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url("ws://192.168.1.64:8887").build();
        NotificationsWebSocket listener = new NotificationsWebSocket(chatArrayAdapter,activity, chatId);
        WebSocket ws = client.newWebSocket(request,listener);

        Log.d("debug","------Starting WebSocket-------");
    }
}

