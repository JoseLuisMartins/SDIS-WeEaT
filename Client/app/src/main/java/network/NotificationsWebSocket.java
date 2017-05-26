package network;


import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Menu;
import android.view.SubMenu;


import com.example.josemartins.sdis_weeat.logic.ChatArrayAdapter;
import com.example.josemartins.sdis_weeat.logic.ChatMessage;
import com.example.josemartins.sdis_weeat.logic.Utils;
import com.google.android.gms.maps.model.LatLng;

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
    private LatLng chatId;
    private Menu menu;

    @Override
    public void onOpen(WebSocket webSocket, Response response) {

        try {
            JSONObject obj = new JSONObject();
            obj.put("lat",chatId.latitude);
            obj.put("long",chatId.longitude);
            obj.put("token", Utils.client.getToken());
            webSocket.send(obj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d("Debug","message: " + text);


        activity.runOnUiThread(() -> {
            try {
                JSONObject messageJson= new JSONObject(text);
                ChatMessage message = new ChatMessage(messageJson);
                Utils.createChatNotification(activity,message);
                chatArrayAdapter.add(message);


                SubMenu m = menu.getItem(0).getSubMenu();
                String user = message.getName();
                boolean addUser = true;
                for (int i = 0; i < m.size(); i++) {
                   String item = (String) m.getItem(i).getTitle();
                   if(item.equals(user)) {
                       addUser = false;
                        break;
                   }
                }
                if(addUser)
                    m.add(user);

            } catch (JSONException e) {
                e.printStackTrace();
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


    public NotificationsWebSocket(ChatArrayAdapter chatArrayAdapter,Activity activity ,LatLng chatId,Menu menu) {
        this.chatArrayAdapter = chatArrayAdapter;
        this.activity = activity;
        this.chatId = chatId;
        this.menu = menu;
    }

    public static  void request(ChatArrayAdapter chatArrayAdapter , Activity activity ,LatLng chatId,Menu menu){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(Utils.webSocketUrl).build();
        NotificationsWebSocket listener = new NotificationsWebSocket(chatArrayAdapter,activity, chatId, menu);
        WebSocket ws = client.newWebSocket(request,listener);

        Log.d("debug","------Starting WebSocket-------");
    }
}
