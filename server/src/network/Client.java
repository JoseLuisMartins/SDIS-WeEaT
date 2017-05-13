package network;

import network.messaging.Message;
import network.messaging.distributor.Distributor;
import network.messaging.distributor.balancer.BalancerDistributor;
import network.messaging.distributor.client.ClientDistributor;
import network.messaging.distributor.server.ServerDistributor;
import org.json.JSONObject;

import java.net.URL;


public class Client {

    public Client(){

        try {
            URL url = new URL("https://192.168.1.64:8000");
            Distributor d = new ClientDistributor(this);

            JSONObject jsonUser = new JSONObject();
            jsonUser.put("name","jose");
            jsonUser.put("email","jose@martins.pato");
            jsonUser.put("image_url","https://www.youtube.com");

            JSONObject jsonUser1 = new JSONObject();
            jsonUser1.put("name","martins");
            jsonUser1.put("email","martins@jeronimo.kek");
            jsonUser1.put("image_url","https://www.google.pt");

            JSONObject jsonAddChat = new JSONObject();
            jsonAddChat.put("lat",554.1545);
            jsonAddChat.put("long",2.454);
            jsonAddChat.put("timestamp",2546);

            JSONObject jsonAddUserToChat = new JSONObject();
            jsonAddUserToChat.put("chat_id",1);
            jsonAddUserToChat.put("member","jose@martins.pato");

            JSONObject jsonAddUserToChat1 = new JSONObject();
            jsonAddUserToChat1.put("chat_id",1);
            jsonAddUserToChat1.put("member","martins@jeronimo.kek");

            JSONObject jsonAddMessage = new JSONObject();
            jsonAddMessage.put("content","o david é xirooo");
            jsonAddMessage.put("chat_id",1);
            jsonAddMessage.put("poster","jose");

            JSONObject jsonChatMembers = new JSONObject();
            jsonChatMembers.put("chat_id",1);

            JSONObject jsonChatMessages = new JSONObject();
            jsonChatMessages.put("content","o david é xirooo");
            jsonChatMessages.put("chat_id",1);
            jsonChatMessages.put("poster","jose");

            Message.SendURLMessage(url, new Message(ServerDistributor.ADD_USER, jsonUser.toString()), d);
            Message.SendURLMessage(url, new Message(ServerDistributor.ADD_USER, jsonUser1.toString()), d);

            Message.SendURLMessage(url, new Message(ServerDistributor.ADD_CHAT_GROUP, jsonAddChat.toString()), d);

            Message.SendURLMessage(url, new Message(ServerDistributor.ADD_CHAT_MEMBER, jsonAddUserToChat.toString()), d);
            Message.SendURLMessage(url, new Message(ServerDistributor.ADD_CHAT_MEMBER, jsonAddUserToChat1.toString()), d);

            Message.SendURLMessage(url, new Message(ServerDistributor.ADD_CHAT_MESSAGE, jsonAddMessage.toString()), d);
            Message.SendURLMessage(url, new Message(ServerDistributor.GET_CHAT_GROUPS, jsonAddMessage.toString()), d);
            Message.SendURLMessage(url, new Message(ServerDistributor.GET_CHAT_MEMBERS, jsonChatMembers.toString()), d);
            Message.SendURLMessage(url, new Message(ServerDistributor.GET_CHAT_MESSAGES, jsonChatMessages.toString()), d);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] s){
        new Client();
    }


}
