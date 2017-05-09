package network.messaging.distributor;

import network.messaging.Message;

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

    public void distribute(Message m){
        if(actions.containsKey(m.getClassID()))
            actions.get(m.getClassID()).execute(m);
        else
            System.out.println("Message Unknown");
    }


}
