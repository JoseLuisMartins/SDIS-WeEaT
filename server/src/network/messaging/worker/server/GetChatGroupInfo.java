package src.network.messaging.worker.server;


import src.network.messaging.worker.Worker;

import com.sun.net.httpserver.HttpExchange;

public class GetChatGroupInfo extends Worker {
    public GetChatGroupInfo(String name) {
        super("GetChatGroupInfo");
    }

    @Override
    public void work(Object obj, Object data) {

    }
}
