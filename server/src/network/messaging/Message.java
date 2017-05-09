package network.messaging;


import com.sun.net.httpserver.HttpExchange;

import java.io.Serializable;

public class Message implements Serializable{

    private int actionID;
    private Object content;
    private transient HttpExchange httpExchange;

    public Message(int actionID, Object content, HttpExchange httpExchange){
        this.actionID = actionID;
        this.content = content;
        this.httpExchange = httpExchange;
    }

    public int getClassID(){ return actionID;}

    public void setHttpExchange(HttpExchange e){
        httpExchange = e;
    }
    public Object getContent(){
        return content;
    }

    public HttpExchange getHttpExchange() {
        return httpExchange;
    }
}
