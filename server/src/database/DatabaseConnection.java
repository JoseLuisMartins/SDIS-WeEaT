package database;

import org.postgresql.util.PSQLException;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class DatabaseConnection {

    public Connection conn = null;

    public DatabaseConnection() {

        connect();

    }

    public void connect(){
        try {

            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/weeat","postgres", "q1w2e3r4t5");
            conn.setAutoCommit(false);
            System.out.println("Opened database successfully");

        } catch (Exception e){
            System.out.println("Could not open database!");
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ChatRoom> get_chatrooms() {

        ArrayList<ChatRoom> res = new ArrayList<ChatRoom>();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM chatroom;" );

            while ( rs.next() ) {

                int id = rs.getInt("id");
                Timestamp date = rs.getTimestamp("date");
                Point location = (Point)rs.getObject("location");
                String creator = rs.getString("creator");

                res.add(new ChatRoom(id,location,creator,date));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    public ArrayList<MessageDB> get_chat_messages(int chat_id) {

        ArrayList<MessageDB> res = new ArrayList<MessageDB>();
        PreparedStatement stmt = null;
        try {

            stmt = conn.prepareStatement("SELECT * FROM message WHERE chat_id = ?;");
            stmt.setInt(1,chat_id);
            ResultSet rs = stmt.executeQuery();

            while ( rs.next() ) {

                int id = rs.getInt("id");
                int r_chat_id = rs.getInt("chat_id");
                String poster = rs.getString("poster");
                String content = rs.getString("content");
                Timestamp date = rs.getTimestamp("date");

                res.add(new MessageDB(id,date,content,r_chat_id,poster));

            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    public ArrayList<ChatMember> get_chat_members(int chat_id) {

        ArrayList<ChatMember> res = new ArrayList<ChatMember>();
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement("SELECT * FROM chat_member WHERE chat_id = ?;");
            stmt.setInt(1,chat_id);
            ResultSet rs = stmt.executeQuery();

            while ( rs.next() ) {

                int r_chat_id = rs.getInt("chat_id");
                String member = rs.getString("member");
                res.add(new ChatMember(r_chat_id,member));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    public void add_chatroom(Point location, Timestamp date,String creator) {

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO chatroom (location, creator, date) VALUES (?, ?, ?)");
            stmt.setObject(1,location);
            stmt.setString(2,creator);
            stmt.setObject(3,date);

            Boolean rs = stmt.execute();

            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void add_chat_member(int id_chat,String member) {

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO chat_member (chat_id, member) VALUES (?, ?)");
            stmt.setInt(1,id_chat);
            stmt.setString(2,member);

            Boolean rs = stmt.execute();

            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void add_user(String username) {

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO user_weeat (username) VALUES (?)");
            stmt.setString(1,username);

            Boolean rs = stmt.execute();

            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void add_message(String content,int chat_id, String poster) {

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement("INSERT INTO message (content,chat_id,poster) VALUES (?,?,?)");
            stmt.setString(1,content);
            stmt.setInt(2,chat_id);
            stmt.setString(3,poster);

            Boolean rs = stmt.execute();

            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void test(){

        Statement stmt = null;
        try {
            stmt = conn.createStatement();

            String sql = "INSERT INTO user_weeat (username) "
                    + "VALUES ('Manuelinho');";
            stmt.executeUpdate(sql);

            sql = "INSERT INTO user_weeat (username) "
                    + "VALUES ('Roberta Freitas');";
            stmt.executeUpdate(sql);

            sql = "INSERT INTO user_weeat (username) "
                    + "VALUES ('Scoopy Potato');";
            stmt.executeUpdate(sql);

            ResultSet rs = stmt.executeQuery( "SELECT * FROM user_weeat;");
            while ( rs.next() ) {
                String username = rs.getString("username");

                System.out.println( "Username = " + username );
            }

            conn.commit();
            rs.close();
            stmt.close();

        } catch (PSQLException e) {
            System.out.println("PostgreSQL error! " + e.getMessage());
            //e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void print(){

        Statement stmt = null;
        try {

            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery( "SELECT * FROM user_weeat;" );
            while ( rs.next() ) {
                String username = rs.getString("username");

                System.out.println( "Username = " + username );
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void main(String args[]) {

        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));

        DatabaseConnection db = new DatabaseConnection();

        db.print();
        DatabaseManager.database_backup();

        db.close();
        DatabaseManager.database_delete();
        DatabaseManager.database_create();
        db.connect();
        //DatabaseManager.database_init();
        DatabaseManager.database_restore();

        db.print();

        System.out.println("Closing...");
        db.close();

    }
}