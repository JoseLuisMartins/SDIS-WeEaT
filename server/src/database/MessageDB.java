package database;

import network.Utils;
import org.json.JSONObject;
import org.postgresql.geometric.PGpoint;

import java.sql.Timestamp;

public class MessageDB {

    public int id;
    public Timestamp date;
    public String content;
    public PGpoint chat_location;
    public String poster;


    public MessageDB(int id, Timestamp date, String content, PGpoint chat_location, String poster) {
        this.id = id;
        this.date = date;
        this.content = content;
        this.chat_location = chat_location;
        this.poster = poster;
    }

    public MessageDB( Timestamp date, String content, PGpoint chat_location, String poster) {
        this.date = date;
        this.content = content;
        this.chat_location = chat_location;
        this.poster = poster;
    }



    public JSONObject toJson(){
        JSONObject res= new JSONObject();
        UserWeeat user = Utils.db.get_user(poster);

        res.put("date","Pass data string");
        res.put("content",content);
        res.put("chat_lat",chat_location.x);
        res.put("chat_long",chat_location.y);
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
                ", chat_lat=" + chat_location.x +
                ", chat_long=" + chat_location.y +
                ", poster='" + poster + '\'' +
                '}';
    }
}
