package network.messaging;





import java.io.*;
import java.util.ArrayList;


public class Message implements Serializable{
    private static final long serialVersionUID = 1L;

    private int actionID;
    private Object content;
    private transient ArrayList<Object> actionObjects;


    public Message(int actionID, Object content){
        this.actionID = actionID;
        this.content = content;

    }


    public int getClassID(){ return actionID;}


    public Object getContent(){
        return content;
    }

    public ArrayList<Object> getActionObjects() {
        return actionObjects;
    }

    public void setActionObjects( ArrayList<Object> actionObject) {
        this.actionObjects = actionObject;
    }
}
