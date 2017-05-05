package network.messaging.worker;

import java.util.HashMap;

/**
 * Created by joao on 5/5/17.
 */
public class WorkerManager {

    public static HashMap<String,Worker> workers = new HashMap<String,Worker>();
    public static DefaultWorker df = new DefaultWorker();

    public static Worker getWorker(String name){
        if(workers.containsKey(name)){
            return workers.get(name);
        }
        return df;
    }

    public static void addWorker(String name, Worker worker){
        workers.put(name,worker);
    }

}
