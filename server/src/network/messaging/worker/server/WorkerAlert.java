package network.messaging.worker.server;

import network.messaging.worker.Worker;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created by joao on 5/5/17.
 */
public class WorkerAlert extends Worker {
    public WorkerAlert() {
        super("WorkerAlert");
    }

    @Override
    public void work(Object obj, Object data) {
        try {
            ((PrintStream)obj).write((byte[])data);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
