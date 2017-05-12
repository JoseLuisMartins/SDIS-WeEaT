package database;

import org.json.JSONObject;

/**
 * Created by PeaceOff on 09-05-2017.
 */
public class ChatMember {

    public int chat_id;
    public String member;

    public ChatMember(int chat_id, String member) {
        this.chat_id = chat_id;
        this.member = member;
    }

    public JSONObject toJson(){
        JSONObject res= new JSONObject();
        res.put("chat_id",chat_id);
        res.put("member",member);

        return res;
    }


    @Override
    public String toString() {
        return "ChatMember{" +
                "chat_id=" + chat_id +
                ", member='" + member + '\'' +
                '}';
    }
}
