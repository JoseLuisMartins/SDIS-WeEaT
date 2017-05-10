package network.messaging.distributor.server;

import network.Server;
import network.messaging.Message;
import network.messaging.distributor.Distributor;

/**
 * Created by joao on 5/10/17.
 */
public class ServerDistributor extends Distributor {

    public static final int REQUEST_CHAT = 0;
    public static final int SET_MODE = 1;

    private Server server;

    public ServerDistributor(Server server){
        this.server = server;
        addAction(REQUEST_CHAT, (Message m) -> chatRequest(m));
        addAction(SET_MODE, (Message m) -> setMode(m));
    }

    public void chatRequest(Message m){

    }

    public void setMode(Message m){
        server.setMode((int)m.getContent());
    }
}
