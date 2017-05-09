package src.network.messaging;


import com.sun.net.httpserver.HttpExchange;

public class Message {

    private String classID;
    private Object content;
    private HttpExchange httpExchange;

    public Message(String classID, Object content, HttpExchange httpExchange){
        this.classID = classID;
        this.content = content;
        this.httpExchange = httpExchange;
    }

    public String getClassID(){
        return classID;
    }

    public Object getContent(){
        return content;
    }

    public HttpExchange getHttpExchange() {
        return httpExchange;
    }
}
