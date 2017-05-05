package network.messaging;

import network.messaging.worker.Worker;
import network.messaging.worker.WorkerManager;
import network.messaging.worker.server.WorkerAlert;

/**
 * Created by joao on 5/5/17.
 */
public class ServerMessageParser extends MessageParser {

    public ServerMessageParser(){
        new WorkerAlert();
    }

    public void ReceiveMessage(Message m ){
        Worker o = WorkerManager.getWorker(m.getClassID());
        o.work(System.out,m.getContent());


    }






}

