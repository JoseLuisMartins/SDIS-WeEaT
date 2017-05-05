package network.messaging.worker;

import java.util.HashMap;

/**
 * Created by joao on 5/5/17.
 */
public abstract class Worker{



    public Worker(String name){
        WorkerManager.addWorker(name, this);
    }

    public abstract void work(Object obj, Object data);


}
