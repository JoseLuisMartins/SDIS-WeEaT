package network.messaging.distributor.server;

import jdk.nashorn.api.scripting.JSObject;
import network.Server;
import network.Utils;
import network.messaging.Message;
import network.messaging.distributor.Distributor;
import org.json.JSONObject;

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
        addAction(ADD_USER, (Message m) -> setMode(m));
        addAction(ADD_CHAT_GROUP, (Message m) -> setMode(m));
        addAction(GET_CHAT_GROUP, (Message m) -> setMode(m));

    }

    
    public void setMode(Message m){
        server.setMode((int)m.getContent());
    }

    public void addUser(Message m){
        JSONObject obj = new JSONObject((String)m.getContent());

        Utils.db.add_user(obj.getString("token"));
    }

    public void setAddChatGroup(Message m){


    }

    public void getChatGroup(Message m){


    }


}
