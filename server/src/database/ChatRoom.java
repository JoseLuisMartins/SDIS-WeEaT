package database;

import jdk.nashorn.api.scripting.JSObject;
import org.json.JSONObject;
import org.postgresql.geometric.PGpoint;

import java.awt.*;
import java.sql.Timestamp;

public class ChatRoom {

    public int id;
    public PGpoint location;
    public Timestamp date;
    public String title;

    public ChatRoom(int _id, PGpoint _location, Timestamp _date,String _title){
        id = _id;
        location = _location;
        date = _date;
        title = _title;
    }

    public JSONObject toJson(){
        JSONObject res= new JSONObject();
        res.put("id",id);
        res.put("lat",location.x);
        res.put("long",location.y);
        res.put("date",date.getTime());
        res.put("title",title);

        return res;
    }


    public Timestamp getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "id=" + id +
                ", location=" + location +
                ", date=" + date +
                ", title=" + title +
                '}';
    }
}
