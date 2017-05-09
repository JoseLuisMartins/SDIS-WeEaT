package src.network;


import com.sun.net.httpserver.HttpServer;

import java.util.HashMap;

public class LoadBalancer {

    private HashMap<String,String> nodes;
    private int port;
    private HttpServer server;


    public LoadBalancer(int port){
        nodes = new HashMap<String, String>();
        this.port = port;
    }

    public void addServer(Server server){

    }

    public String getServer(){
        return null;
    }

}
