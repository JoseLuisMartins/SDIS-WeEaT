package network.messaging.distributor.server;

import jdk.nashorn.api.scripting.JSObject;
import network.Server;
import network.Utils;
import network.messaging.Message;
import network.messaging.distributor.Distributor;
import org.json.JSONObject;
import org.postgresql.geometric.PGpoint;

import java.sql.Timestamp;


/**
 * Created by joao on 5/10/17.
 */
public class ServerDistributor extends Distributor {


    public static final int SET_MODE = 1;
    public static final int ADD_USER = 2;
    public static final int ADD_CHAT_GROUP = 3;
    public static final int GET_CHAT_GROUP = 4;



    private Server server;

    public ServerDistributor(Server server){
        this.server = server;
        addAction(SET_MODE, (Message m) -> setMode(m));
        addAction(ADD_USER, (Message m) -> addUser(m));
        addAction(ADD_CHAT_GROUP, (Message m) -> addChatGroup(m));
        addAction(GET_CHAT_GROUP, (Message m) -> getChatGroup(m));

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

    public void getChatGroup(Message m){
        JSONObject obj = new JSONObject((String)m.getContent());

        Utils.db.add_user(obj.getString("token"));

    }


}
