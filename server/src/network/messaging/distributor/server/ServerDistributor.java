package network.messaging.distributor.server;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import network.GoogleLoginChecker;
import network.Server;
import network.Utils;
import network.messaging.Message;
import network.messaging.distributor.Distributor;
import network.messaging.distributor.client.ClientDistributor;

import org.json.JSONObject;
import org.postgresql.geometric.PGpoint;

import java.io.IOException;
import java.sql.Timestamp;

import static network.GoogleLoginChecker.googleLoginChecker;


public class ServerDistributor extends Distributor {


    public static final int SET_MODE = 1;
    public static final int ADD_USER = 2;
    public static final int ADD_CHAT_GROUP = 3;
    public static final int ADD_CHAT_MEMBER = 4;
    public static final int ADD_CHAT_MESSAGE = 5;
    public static final int GET_CHAT_GROUPS = 6;
    public static final int GET_CHAT_MEMBERS = 7;
    public static final int GET_CHAT_MESSAGES = 8;

    private Server server;

    public ServerDistributor(Server server){
        this.server = server;
        addAction(SET_MODE, (Message m) -> setMode(m));
        addAction(ADD_USER, (Message m) -> addUser(m));
        addAction(ADD_CHAT_GROUP, (Message m) -> addChatGroup(m));
        addAction(ADD_CHAT_MEMBER, (Message m) -> addChatMember(m));
        addAction(ADD_CHAT_MESSAGE, (Message m) -> addChatMessage(m));
        addAction(GET_CHAT_GROUPS, (Message m) -> getChatGroups(m));
        addAction(GET_CHAT_MEMBERS, (Message m) -> getChatMembers(m));
        addAction(GET_CHAT_MESSAGES, (Message m) -> getChatMessages(m));
    }


    public void setMode(Message m){
        server.setMode((int)m.getContent());
    }

    public void addUser(Message m){
       JSONObject userInfo = m.getUserInfo();

        Utils.db.add_user((String) userInfo.get("name"), (String) userInfo.get("email") , (String) userInfo.get("picture"));
        Utils.db.debug_users();

        try {
            sendMessage(m.getHttpExchange().getResponseBody(),new Message(ClientDistributor.RESPONSE,"Ah Gay: add user"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addChatGroup(Message m){
        JSONObject obj = new JSONObject((String)m.getContent());


        PGpoint point = new PGpoint(obj.getDouble("lat"),obj.getDouble("long"));
        Timestamp ts = new Timestamp(obj.getLong("timestamp"));


        Utils.db.add_chatroom(point,ts);

        Utils.db.debug_chatrooms();

        try {
            sendMessage(m.getHttpExchange().getResponseBody(),new Message(ClientDistributor.RESPONSE,"Ah Gay: chat group"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void addChatMember(Message m){
        JSONObject obj = new JSONObject((String)m.getContent());

        int chat_id = obj.getInt("chat_id");
        String member = obj.getString("member");


        Utils.db.add_chat_member(chat_id,member);
        Utils.db.debug_chatmembers();

        try {
            sendMessage(m.getHttpExchange().getResponseBody(),new Message(ClientDistributor.RESPONSE,"Ah Gay:chat member"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean checkJson(JSONObject obj,String ... param){

        for (String s :param){
            if(!obj.has(s))
                return false;
        }

        return true;
    }

    private void addChatMessage(Message m) {
        JSONObject obj = new JSONObject((String)m.getContent());
        JSONObject userInfo = m.getUserInfo();

        if(!checkJson(obj,"content","chat_id") && checkJson(userInfo,"email")) {
            try {
                sendMessage(m.getHttpExchange().getResponseBody(), new Message(ClientDistributor.RESPONSE, "Bad request"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }


        String content = obj.getString("content");
        int chat_id = obj.getInt("chat_id");


        System.out.println(obj.toString());
        System.out.println(userInfo.toString());

        String poster = (String) userInfo.get("email");

        Utils.db.add_message(content, chat_id, poster);

        Utils.db.debug_chatmessages();

        try {
            sendMessage(m.getHttpExchange().getResponseBody(), new Message(ClientDistributor.RESPONSE, "Ah Gay:messages"));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    public void getChatGroups(Message m){


       JSONObject result = Utils.db.get_chatrooms();


        System.out.println(result.toString());

        Utils.db.debug_chatmessages();

        try {
            sendMessage(m.getHttpExchange().getResponseBody(),new Message(ClientDistributor.RESPONSE,result.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void getChatMembers(Message m){

        JSONObject obj = new JSONObject((String)m.getContent());

        int chat_id = obj.getInt("chat_id");

        JSONObject res = Utils.db.get_chat_members(chat_id);

        try {
            sendMessage(m.getHttpExchange().getResponseBody(),new Message(ClientDistributor.RESPONSE,res.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getChatMessages(Message m){

        JSONObject obj = new JSONObject((String)m.getContent());

        int chat_id = obj.getInt("chat_id");

        JSONObject res = Utils.db.get_chat_messages(chat_id);

        try {
            sendMessage(m.getHttpExchange().getResponseBody(),new Message(ClientDistributor.RESPONSE,res.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
