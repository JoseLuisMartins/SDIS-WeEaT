package network.messaging;

/**
 * Created by joao on 5/5/17.
 */
public class Message {

    private String classID;
    private Object content;

    public Message(String classID, Object content){
        this.classID = classID;
        this.content = content;
    }

    public String getClassID(){
        return classID;
    }

    public Object getContent(){
        return content;
    }

}
