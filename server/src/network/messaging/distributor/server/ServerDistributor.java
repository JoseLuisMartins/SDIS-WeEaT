package network.messaging.distributor.server;

import database.ChatMember;
import database.ChatRoom;
import database.MessageDB;
import network.Server;
import network.Utils;
import network.messaging.Message;
import network.messaging.distributor.Distributor;
import org.json.JSONObject;
import org.postgresql.geometric.PGpoint;

import java.sql.Timestamp;
import java.util.ArrayList;


public class ServerDistributor extends Distributor {


    public static final int SET_MODE = 1;
    public static final int ADD_USER = 2;
    public static final int ADD_CHAT_GROUP = 3;
    public static final int ADD_CHAT_MEMBER = 4;
    public static final int ADD_CHAT_MESSAGE = 5;
    public static final int GET_CHAT_GROUP = 6;
    public static final int GET_CHAT_MEMBER = 7;
    public static final int GET_CHAT_MESSAGE = 8;

    private Server server;

    public ServerDistributor(Server server){
        this.server = server;
        addAction(SET_MODE, (Message m) -> setMode(m));
        addAction(ADD_USER, (Message m) -> addUser(m));
        addAction(ADD_CHAT_GROUP, (Message m) -> addChatGroup(m));
        addAction(ADD_CHAT_MEMBER, (Message m) -> addChatMember(m));
        addAction(ADD_CHAT_MESSAGE, (Message m) -> addChatMessage(m));
        addAction(GET_CHAT_GROUP, (Message m) -> getChatGroup(m));
        addAction(GET_CHAT_MEMBER, (Message m) -> getChatMember(m));
        addAction(GET_CHAT_MESSAGE, (Message m) -> getChatMessage(m));
    }


    public void setMode(Message m){
        server.setMode((int)m.getContent());
    }

    public void addUser(Message m){
        JSONObject obj = new JSONObject((String)m.getContent());

        Utils.db.add_user(obj.getString("token"));
    }

    public void addChatGroup(Message m){
        JSONObject obj = new JSONObject((String)m.getContent());


        PGpoint point = new PGpoint(obj.getDouble("lat"),obj.getDouble("long"));
        Timestamp ts = new Timestamp(obj.getLong("timestamp"));
        String creator = obj.getString("lat");


        Utils.db.add_chatroom(point,ts,creator);

    }

    public void addChatMember(Message m){
        JSONObject obj = new JSONObject((String)m.getContent());


        int chat_id = obj.getInt("chat_id");
        String member = obj.getString("member");


        Utils.db.add_chat_member(chat_id,member);

    }

    private void addChatMessage(Message m) {
        JSONObject obj = new JSONObject((String)m.getContent());


        String content = obj.getString("content");
        int chat_id = obj.getInt("chat_id");
        String poster = obj.getString("poster");

        Utils.db.add_message(content,chat_id,poster);
    }

    public JSONObject getChatGroup(Message m){

        JSONObject obj = new JSONObject((String)m.getContent());

        ArrayList<ChatRoom> result = Utils.db.get_chatrooms();

        JSONObject response = new JSONObject(result);

        return response;
    }

    public JSONObject getChatMember(Message m){

        JSONObject obj = new JSONObject((String)m.getContent());

        int chat_id = obj.getInt("chat_id");

        ArrayList<ChatMember> result = Utils.db.get_chat_members(chat_id);

        JSONObject response = new JSONObject(result);

        return response;
    }

    public JSONObject getChatMessage(Message m){

        JSONObject obj = new JSONObject((String)m.getContent());

        int chat_id = obj.getInt("chat_id");

        ArrayList<MessageDB> result = Utils.db.get_chat_messages(chat_id);

        JSONObject response = new JSONObject(result);

        return response;
    }


}
