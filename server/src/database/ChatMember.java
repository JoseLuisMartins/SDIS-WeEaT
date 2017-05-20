package database;

import network.Utils;
import org.json.JSONObject;
import org.postgresql.geometric.PGpoint;

public class ChatMember {

    public PGpoint chat_location;
    public String member;

    public ChatMember(PGpoint chat_location, String member) {
        this.chat_location = chat_location;
        this.member = member;
    }

    public JSONObject toJson(){
        JSONObject res= new JSONObject();
        UserWeeat user = Utils.db.get_user(member);

        res.put("chat_lat",chat_location.x);
        res.put("chat_long",chat_location.y);
        res.put("name",user.getUsername());


        return res;
    }

    @Override
    public String toString() {
        return "ChatMember{" +
                "chat_lat=" + chat_location.x +
                "chat_long=" + chat_location.y +
                ", member='" + member + '\'' +
                '}';
    }
}
