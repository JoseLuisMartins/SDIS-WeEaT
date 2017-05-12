package network.messaging.distributor;


import network.messaging.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Created by joao on 5/9/17.
 */
public class Distributor {

    public  interface Action{
        void execute(Message m);
    }

    protected HashMap<Integer, Action> actions = new HashMap<>();

    protected void addAction(int id, Action action){
        actions.put(id, action);
    }


    //just for server-> add to the response body
    protected static void sendMessage(OutputStream o, Message m) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(o);
        out.writeObject(m);
        out.close();
    }


    public void distribute(Message m){
        if(actions.containsKey(m.getClassID()))
            actions.get(m.getClassID()).execute(m);
        else
            System.out.println("Message Unknown");
    }


}
