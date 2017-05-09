package network.messaging.worker.server;


import network.ServerHttpHandler;
import network.messaging.worker.Worker;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class AuthUser extends Worker{
    public AuthUser() {
        super("AuthUser");
    }

    @Override
    public void work(Object obj, Object data) {
        //work


        //response
        try {
            ServerHttpHandler.sendResponse((HttpExchange) obj,200,"Authenticating user");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
