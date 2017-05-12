package database;

import org.postgresql.geometric.PGpoint;

import java.awt.*;
import java.sql.Timestamp;

public class ChatRoom {

    public int id;
    public PGpoint location;
    public Timestamp date;

    public ChatRoom(int _id, PGpoint _location, Timestamp _date){
        id = _id;
        location = _location;
        date = _date;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "id=" + id +
                ", location=" + location +
                ", date=" + date +
                '}';
    }
}
