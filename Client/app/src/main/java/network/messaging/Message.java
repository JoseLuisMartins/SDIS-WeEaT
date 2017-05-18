package network.messaging;



import java.io.*;



public class Message implements Serializable{
    private static final long serialVersionUID = 1L;

    private int actionID;
    private Object content;



    public Message(int actionID, Object content){
        this.actionID = actionID;
        this.content = content;
    }


    public int getClassID(){ return actionID;}


    public Object getContent(){
        return content;
    }



}
