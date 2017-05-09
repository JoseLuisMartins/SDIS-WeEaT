package network.messaging.worker;

import com.sun.net.httpserver.HttpExchange;


public abstract class Worker{

    public Worker(String name){
        WorkerManager.addWorker(name, this);
    }

    public abstract void work(Object obj, Object data);


}
