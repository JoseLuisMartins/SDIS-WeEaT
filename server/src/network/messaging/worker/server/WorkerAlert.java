package src.network.messaging.worker.server;

import src.network.messaging.worker.Worker;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;


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
