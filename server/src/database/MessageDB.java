package database;

import network.Utils;
import org.json.JSONObject;

import java.sql.Timestamp;

public class MessageDB {

    public int id;
    public Timestamp date;
    public String content;
    public int chat_id;
    public String poster;


    public MessageDB(int id, Timestamp date, String content, int chat_id, String poster) {
        this.id = id;
        this.date = date;
        this.content = content;
        this.chat_id = chat_id;
        this.poster = poster;
    }

    public MessageDB( Timestamp date, String content, int chat_id, String poster) {
        this.date = date;
        this.content = content;
        this.chat_id = chat_id;
        this.poster = poster;
    }



    public JSONObject toJson(){
        JSONObject res= new JSONObject();
        UserWeeat user = Utils.db.get_user(poster);

        res.put("date","Pass data string");
        res.put("content",content);
        res.put("chat_id",chat_id);
        res.put("poster",poster);
        res.put("name",user.getUsername());
        res.put("imageUrl",user.getImage_url());

        return res;
    }

    @Override
    public String toString() {
        return "MessageDB{" +
                "id=" + id +
                ", date=" + date +
                ", content='" + content + '\'' +
                ", chat_id=" + chat_id +
                ", poster='" + poster + '\'' +
                '}';
    }
}
