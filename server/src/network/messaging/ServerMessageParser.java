package src.network.messaging;


import src.network.messaging.worker.Worker;
import src.network.messaging.worker.WorkerManager;
import src.network.messaging.worker.server.AuthUser;
import src.network.messaging.worker.server.WorkerAlert;


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

