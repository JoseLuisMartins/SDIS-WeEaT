package network.messaging;


import network.messaging.worker.Worker;
import network.messaging.worker.WorkerManager;
import network.messaging.worker.server.AuthUser;
import network.messaging.worker.server.WorkerAlert;


public class ServerMessageParser extends MessageParser {

    public ServerMessageParser(){
        new WorkerAlert();
        new AuthUser();
    }

    public void ReceiveMessage(Message m ){
        Worker o = WorkerManager.getWorker(m.getClassID());
        o.work(m.getHttpExchange(),m.getContent());


    }






}

