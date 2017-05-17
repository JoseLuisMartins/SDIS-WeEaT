package database;

import jdk.nashorn.api.scripting.JSObject;
import network.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.postgresql.geometric.PGpoint;
import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseConnection {

    public Connection conn = null;

    /*  SELECT id
        FROM table
        WHERE IF(@above, datecol < @param, datecol > @param)
        ORDER BY IF (@above. datecol ASC, datecol DESC)
        LIMIT 1

        SELECT EXISTS(SELECT 1 FROM contact WHERE id=12);


    * Commands to change the postgres user password
    * sudo -u postgres psql template1
    * ALTER USER postgres with encrypted password 'sua_senha';
    * sudo systemctl restart postgresql.service
    */
    public DatabaseConnection() {
        DatabaseManager.database_create();
        DatabaseManager.database_init();
        DatabaseManager.database_restore();
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
        DatabaseManager.start_backup();
        DatabaseManager.start_midnight_delete();
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JSONArray get_chatroom_data(ResultSet rs) throws SQLException {

        JSONArray res = new JSONArray();

        while ( rs.next() ) {

            int id = rs.getInt("id");
            Timestamp date = rs.getTimestamp("date");
            PGpoint location = (PGpoint)rs.getObject("location");
            String title = rs.getString("title");

            res.put(new ChatRoom(id,location,date,title).toJson());
        }

        rs.close();

        return res;
    }

    public JSONObject get_chatrooms_by_location(double x, double y) {

        JSONArray jsonArray = null;


        PGpoint user_location = new PGpoint(x,y);
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("SELECT *\n" +
                                            "FROM chatroom\n" +
                                            "ORDER BY ? <-> location ASC\n" +
                                            "LIMIT 25;");
            stmt.setObject(1,user_location);

            ResultSet rs = stmt.executeQuery();

            jsonArray = get_chatroom_data(rs);

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JSONObject res = new JSONObject();
        res.put("chats",jsonArray);

        return res;
    }

    public JSONObject get_chatrooms() {

        JSONArray jsonArray = null;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM chatroom;" );


            jsonArray = get_chatroom_data(rs);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JSONObject res = new JSONObject();
        res.put("chats",jsonArray);

        return res;
    }

    public JSONObject get_chat_messages(int chat_id) {

        JSONArray jsonArray = new JSONArray();

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


                jsonArray.put(new MessageDB(id,date,content,r_chat_id,poster).toJson());

            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JSONObject res = new JSONObject();
        res.put("messages",jsonArray);

        return res;
    }

    public JSONObject get_chat_members(int chat_id) {

        JSONArray jsonArray = new JSONArray();

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement("SELECT * FROM chat_member WHERE chat_id = ?;");
            stmt.setInt(1,chat_id);
            ResultSet rs = stmt.executeQuery();

            while ( rs.next() ) {

                int r_chat_id = rs.getInt("chat_id");
                String member = rs.getString("member");

                jsonArray.put(new ChatMember(r_chat_id,member).toJson());
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        JSONObject res = new JSONObject();
        res.put("chat_users",jsonArray);

        return res;
    }

    public void add_chatroom(PGpoint location, Timestamp date,String user,String title) {

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO chatroom (location, date, title) VALUES (point(?,?), ?, ?) RETURNING id;");
            stmt.setDouble(1,location.x);
            stmt.setDouble(2,location.y);
            stmt.setObject(3,date);
            stmt.setString(4,title);

            ResultSet rs = stmt.executeQuery();
            int new_id = rs.getInt("id");

            add_chat_member(new_id,user);

            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        DatabaseManager.setOutdated(true);

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

        DatabaseManager.setOutdated(true);
    }

    public void add_user(String username,String email,String image_url) {

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO user_weeat (username,email,image_url) VALUES (?,?,?)");
            stmt.setString(1,username);
            stmt.setString(2,email);
            stmt.setString(3,image_url);

            Boolean rs = stmt.execute();

            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        DatabaseManager.setOutdated(true);
    }

    public void add_message(String content,int chat_id, String poster) {

        PreparedStatement stmt = null;
        PreparedStatement check_stmt = null;

        try {

            check_stmt = conn.prepareStatement("SELECT EXISTS(SELECT 1 FROM user_weeat WHERE email = ? ;");
            check_stmt.setString(1,poster);

            if(check_stmt.execute()) {
                stmt = conn.prepareStatement("INSERT INTO message (content,chat_id,poster) VALUES (?,?,?)");
                stmt.setString(1, content);
                stmt.setInt(2, chat_id);
                stmt.setString(3, poster);

                Boolean rs = stmt.execute();

                stmt.close();
            } else {
                throw new Utils.UserIsNotMemberException();
            }

            check_stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Utils.UserIsNotMemberException e) {
            System.out.println("User " + poster + " is not memeber of chat with id " + chat_id);
        }

        DatabaseManager.setOutdated(true);
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
                String email = rs.getString("email");
                System.out.println( "Email = " + email );
                String image_url = rs.getString("image_url");
                System.out.println( "Image_url = " + image_url );
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
                String title = rs.getString("title");

                ChatRoom cr = new ChatRoom(id,location,date,title);
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

        System.out.println("Working Directory = " + System.getProperty("user.dir"));

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

        System.out.println("Closing...");
        db.close();
    }
}