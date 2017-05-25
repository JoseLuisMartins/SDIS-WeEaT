package network.messaging.distributor.server;

import database.ChatMember;
import database.ChatRoom;
import database.MessageDB;
import database.UserWeeat;
import network.ServerWeEat;
import network.Utils;
import network.messaging.Message;
import network.messaging.distributor.Distributor;
import network.messaging.distributor.client.ClientDistributor;

import network.notification.NotificationWebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.postgresql.geometric.PGpoint;

import java.io.IOException;
import java.sql.Timestamp;

import static network.Utils.isJsonValid;


public class ServerDistributor extends Distributor {


    public static final int SET_MODE = 1;
    public static final int ADD_USER = 2;
    public static final int ADD_CHAT_GROUP = 3;
    public static final int ADD_CHAT_MEMBER = 4;
    public static final int ADD_CHAT_MESSAGE = 5;
    public static final int GET_CHAT_GROUPS = 6;
    public static final int GET_CHAT_MEMBERS = 7;
    public static final int GET_CHAT_DATA = 8;

    private ServerWeEat serverWeEat;

    public ServerDistributor(ServerWeEat serverWeEat){
        this.serverWeEat = serverWeEat;
        addAction(SET_MODE, (Message m) -> setMode(m));
        addAction(ADD_USER, (Message m) -> addUser(m));
        addAction(ADD_CHAT_GROUP, (Message m) -> addChatGroup(m));
        addAction(ADD_CHAT_MEMBER, (Message m) -> addChatMember(m));
        addAction(ADD_CHAT_MESSAGE, (Message m) -> addChatMessage(m));
        addAction(GET_CHAT_GROUPS, (Message m) -> getChatGroups(m));
        addAction(GET_CHAT_MEMBERS, (Message m) -> getChatMembers(m));
        addAction(GET_CHAT_DATA, (Message m) -> getChatData(m));
    }


    public void setMode(Message m){
        //serverWeEat.setMode((int)m.getContent());
    }

    public void addUser(Message m){
        JSONObject userInfo = m.getUserInfo();

        if( !isJsonValid(userInfo,"email","name","picture")) {
            try {
                System.out.println("Bad Request: Json does not contain all the necessary information");
                sendMessage(m.getHttpExchange().getResponseBody(), new Message(ClientDistributor.RESPONSE, "Bad request"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        Utils.db.add_user(new UserWeeat((String) userInfo.get("name"), (String) userInfo.get("email") , (String) userInfo.get("picture")));
        Utils.db.debug_users();

        try {
            sendMessage(m.getHttpExchange().getResponseBody(),new Message(ClientDistributor.RESPONSE,"Ah Gay: add user"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addChatGroup(Message m){

        JSONObject obj = new JSONObject((String)m.getContent());

        if( !isJsonValid(obj,"lat","long","timestamp","title")) {
            try {
                System.out.println("Bad Request: Json does not contain all the necessary information");
                sendMessage(m.getHttpExchange().getResponseBody(), new Message(ClientDistributor.RESPONSE, "Bad request"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        PGpoint point = new PGpoint(obj.getDouble("lat"),obj.getDouble("long"));
        Timestamp ts = new Timestamp(obj.getLong("timestamp"));
        System.out.println("Adding chat: " + "long: " + obj.getLong("timestamp") +  "timestamp: " + ts.getTime());
        String title = obj.getString("title");

        Utils.db.add_chatroom(new ChatRoom(-1,point,ts, title));

        Utils.db.debug_chatrooms();


        try {
           sendMessage(m.getHttpExchange().getResponseBody(),new Message(ClientDistributor.RESPONSE,"Ah Gay: chat group"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void addChatMember(Message m){
        JSONObject obj = new JSONObject((String)m.getContent());

        if( !isJsonValid(obj,"lat","long","member")) {
            try {
                System.out.println("Bad Request: Json does not contain all the necessary information");
                sendMessage(m.getHttpExchange().getResponseBody(), new Message(ClientDistributor.RESPONSE, "Bad request"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        PGpoint chat_location = new PGpoint(obj.getDouble("lat"),obj.getDouble("long"));
        String member = obj.getString("member");


        Utils.db.add_chat_member(new ChatMember(chat_location,member));
        Utils.db.debug_chatmembers();

        try {
            sendMessage(m.getHttpExchange().getResponseBody(),new Message(ClientDistributor.RESPONSE,"Ah Gay:chat member"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private void addChatMessage(Message m) {

        JSONObject obj = new JSONObject((String)m.getContent());
        JSONObject userInfo = m.getUserInfo();

        if(!(isJsonValid(obj,"content","lat","long") && isJsonValid(userInfo,"email","name","picture"))) {
            try {
                System.out.println("Bad Request: Json does not contain all the necessary information");
                sendMessage(m.getHttpExchange().getResponseBody(), new Message(ClientDistributor.RESPONSE, "Bad request"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        //create chat message
        String content = obj.getString("content");
        PGpoint chat_location = new PGpoint(obj.getDouble("lat"),obj.getDouble("long"));
        String poster = (String) userInfo.get("email");

        MessageDB chatMessage = new MessageDB(null,content,chat_location,poster);

        Utils.db.add_message(chatMessage);
        Utils.db.debug_chatmessages();

        try {
            //message notification
            NotificationWebSocketServer.sendAll(chatMessage.toJson().toString(),chat_location);
            //response
            sendMessage(m.getHttpExchange().getResponseBody(), new Message(ClientDistributor.RESPONSE, "Ah Gay:messages"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void getChatGroups(Message m){

        System.out.println("Getting Chat Groups");

        JSONObject result = Utils.db.get_chatrooms();

        System.out.println(result.toString());

        Utils.db.debug_chatrooms();

        try {
            sendMessage(m.getHttpExchange().getResponseBody(),new Message(ClientDistributor.FILL_MAP_MARKERS,result.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getChatMembers(Message m){

        JSONObject obj = new JSONObject((String)m.getContent());


        if(!isJsonValid(obj,"lat","long")) {
            try {
                System.out.println("Bad Request: Json does not contain all the necessary information");
                sendMessage(m.getHttpExchange().getResponseBody(), new Message(ClientDistributor.RESPONSE, "Bad request"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        JSONObject res= new JSONObject();
        JSONArray chatMembers = Utils.db.get_chat_members(obj.getDouble("lat"),obj.getDouble("long"));
        res.put("chatMembers",chatMembers);
        try {
            sendMessage(m.getHttpExchange().getResponseBody(),new Message(ClientDistributor.RESPONSE,res.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getChatData(Message m){
        System.out.println("get chat messages");
        //ADD USER TO CHAT
        JSONObject obj = new JSONObject((String)m.getContent());
        JSONObject userInfo =  m.getUserInfo();

        if(!isJsonValid(obj,"lat","long") && isJsonValid(userInfo,"email")) {
            try {
                System.out.println("Bad Request: Json does not contain all the necessary information");
                sendMessage(m.getHttpExchange().getResponseBody(), new Message(ClientDistributor.RESPONSE, "Bad request"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        double latitude = obj.getDouble("lat");
        double longitude = obj.getDouble("long");

        PGpoint chat_location = new PGpoint(latitude ,longitude);
        String member =  (String) userInfo.get("email");


        //Add chat menber to the chat if it isnt already there
        Utils.db.add_chat_member(new ChatMember(chat_location,member));
        Utils.db.debug_chatmembers();

        JSONObject res= new JSONObject();
        //Return the messages to the user
        JSONArray messages = Utils.db.get_chat_messages(chat_location);
        res.put("messages",messages);
        System.out.println(messages.toString());

        //Return chat members to the user
        JSONArray chatMembers = Utils.db.get_chat_members(latitude,longitude);
        res.put("chatMembers",chatMembers);


        try {
            sendMessage(m.getHttpExchange().getResponseBody(),new Message(ClientDistributor.UPDATE_CHAT,res.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
