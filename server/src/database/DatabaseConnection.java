package database;

import org.postgresql.geometric.PGpoint;
import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseConnection {

    public Connection conn = null;

    /*
    * Commands to change the postgres user password
    * sudo -u postgres psql template1
    * ALTER USER postgres with encrypted password 'sua_senha';
    * sudo systemctl restart postgresql.service
    */
    public DatabaseConnection() {
        DatabaseManager.database_create();
        DatabaseManager.database_init();
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
                PGpoint location = (PGpoint)rs.getObject("location");
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

    public void add_chatroom(PGpoint location, Timestamp date,String creator) {

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO chatroom (location, creator, date) VALUES (point(?,?), ?, ?)");
            stmt.setDouble(1,location.x);
            stmt.setDouble(2,location.y);
            stmt.setString(3,creator);
            stmt.setObject(4,date);

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

    public void debug_users(){

        Statement stmt = null;
        try {

            stmt = conn.createStatement();

            System.out.println("*** USER_WEEAT ***");
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

    public void debug_chatrooms(){

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM chatroom;" );

            System.out.println("*** CHATROOMS ***");
            while ( rs.next() ) {

                int id = rs.getInt("id");
                Timestamp date = rs.getTimestamp("date");
                PGpoint location = (PGpoint)rs.getObject("location");
                String creator = rs.getString("creator");

                ChatRoom cr = new ChatRoom(id,location,creator,date);
                System.out.println(cr.toString());
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void debug_chatmembers(){

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM chat_member;" );

            System.out.println("*** CHAT_MEMBERS ***");
            while ( rs.next() ) {

                int r_chat_id = rs.getInt("chat_id");
                String member = rs.getString("member");
                ChatMember cm = new ChatMember(r_chat_id,member);
                System.out.println(cm.toString());
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void debug_chatmessages(){

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM message;" );

            System.out.println("*** CHAT_MESSAGES ***");
            while ( rs.next() ) {

                int id = rs.getInt("id");
                int r_chat_id = rs.getInt("chat_id");
                String poster = rs.getString("poster");
                String content = rs.getString("content");
                Timestamp date = rs.getTimestamp("date");

                MessageDB mdb = new MessageDB(id,date,content,r_chat_id,poster);
                System.out.println(mdb.toString());
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

        db.close();
        DatabaseManager.database_delete();
        DatabaseManager.database_create();
        DatabaseManager.database_init();
        db.connect();
        db.debug_users();
        System.out.println(db.get_chatrooms().toString());
        System.out.println(db.get_chat_members(1).toString());
        System.out.println(db.get_chat_members(2).toString());
        System.out.println(db.get_chat_messages(1).toString());
        System.out.println(db.get_chat_messages(2).toString());

        System.out.println("--------------------------------------------------------------------------------");

        db.add_user("Marcelinho");
        db.add_user("josezinho");
        db.add_user("joaozinho");
        db.add_user("davidzinho");

        db.add_chatroom(new PGpoint(1,32),new Timestamp(Long.parseLong("4124214")),"Marcelinho");
        db.add_chatroom(new PGpoint(6,9),new Timestamp(Long.parseLong("24124214")),"josezinho");

        db.add_chat_member(1,"joaozinho");
        db.add_chat_member(1,"davidzinho");
        db.add_chat_member(2,"joaozinho");
        db.add_chat_member(2,"davidzinho");

        db.add_message("Bom dia!",1,"davidzinho");
        db.add_message("Batatas!",1,"joaozinho");
        db.add_message("Batatas!",2,"joaozinho");
        db.add_message("Bom dia!",2,"davidzinho");

        db.debug_users();
        System.out.println(db.get_chatrooms().toString());
        System.out.println(db.get_chat_members(1).toString());
        System.out.println(db.get_chat_members(2).toString());
        System.out.println(db.get_chat_messages(1).toString());
        System.out.println(db.get_chat_messages(2).toString());

        System.out.println("Closing...");
        db.close();
    }
}