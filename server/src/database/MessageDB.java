package database;

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
