package database;

import org.postgresql.geometric.PGpoint;

import java.awt.*;
import java.sql.Timestamp;

public class ChatRoom {

    public int id;
    public PGpoint location;
    public String creator;
    public Timestamp date;

    public ChatRoom(int _id, PGpoint _location, String _creator, Timestamp _date){
        id = _id;
        location = _location;
        creator = _creator;
        date = _date;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "id=" + id +
                ", location=" + location +
                ", creator='" + creator + '\'' +
                ", date=" + date +
                '}';
    }
}
