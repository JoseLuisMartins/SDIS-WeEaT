package network.messaging.worker.server;


import network.messaging.worker.Worker;

import com.sun.net.httpserver.HttpExchange;

public class JoinChatGroup extends Worker{
    public JoinChatGroup(String name) {
        super("JoinChatGroup");
    }

    @Override
    public void work(Object obj, Object data) {

    }
}
