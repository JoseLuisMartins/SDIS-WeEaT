package database;

import network.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.postgresql.geometric.PGpoint;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.sql.*;
import java.sql.Timestamp;

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
    public DatabaseConnection(boolean restore) {

        DatabaseManager.database_create();
        DatabaseManager.database_init();


        if(restore && Files.exists(Paths.get(System.getProperty("user.dir") + File.separator + "received.backup"))) {
            System.out.println("Restoring Database in DatabaseConnection Constructor!");
            DatabaseManager.database_restore();
        }
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

    public  UserWeeat get_user(String email) {

        UserWeeat user = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM user_weeat WHERE email = ?;");
            stmt.setObject(1,email);

            ResultSet rs = stmt.executeQuery();

            rs.next();
            user = new UserWeeat(rs.getString("username"),rs.getString("email"),rs.getString("image_url"));

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }


    public  ChatRoom get_chat(PGpoint location) {

        ChatRoom chat = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM chatroom WHERE location ~= ?;");
            stmt.setObject(1,location);

            ResultSet rs = stmt.executeQuery();
            rs.next();

            chat = new ChatRoom(rs.getInt("id"),(PGpoint)rs.getObject("location"),rs.getTimestamp("date"), rs.getString("title"));

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chat;
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

    public JSONArray get_chat_messages(PGpoint chat_location) {

        JSONArray jsonArray = new JSONArray();

        PreparedStatement stmt = null;
        try {

            stmt = conn.prepareStatement("SELECT * FROM message WHERE chat_location ~= ?;");
            stmt.setObject(1,chat_location);
            ResultSet rs = stmt.executeQuery();

            while ( rs.next() ) {

                int id = rs.getInt("id");
                PGpoint r_chat_location = (PGpoint)rs.getObject("chat_location");
                String poster = rs.getString("poster");
                String content = rs.getString("content");
                Timestamp date = rs.getTimestamp("date");


                jsonArray.put(new MessageDB(id,date,content,r_chat_location,poster).toJson());

            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return jsonArray;
    }

    public JSONArray get_chat_members(double x,double y) {

        PGpoint location = new PGpoint(x,y);
        JSONArray jsonArray = new JSONArray();

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement("SELECT * FROM chat_member WHERE chat_location ~= ?;");
            stmt.setObject(1,location);
            ResultSet rs = stmt.executeQuery();

            while ( rs.next() ) {

                PGpoint r_chat_location = (PGpoint)rs.getObject("chat_location");
                String member = rs.getString("member");

                jsonArray.put(new ChatMember(r_chat_location,member).toJson());
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }



        return jsonArray;
    }

    public void add_chatroom(ChatRoom cr) {

        PGpoint location = cr.location;
        Timestamp date = cr.date;
        String title = cr.title;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO chatroom (location, date, title) VALUES (point(?,?), ?, ?);");
            stmt.setDouble(1,location.x);
            stmt.setDouble(2,location.y);
            stmt.setObject(3,date);
            stmt.setString(4,title);
            stmt.execute();

            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        DatabaseManager.setOutdated(true);

    }

    public void add_chat_member(ChatMember cm) {

        PGpoint location_chat = cm.chat_location;
        String member = cm.member;
        PreparedStatement stmt = null;
        PreparedStatement check_stmt = null;

        try {

            check_stmt = conn.prepareStatement("SELECT * FROM chat_member WHERE  member = ? AND chat_location ~= ?;");
            check_stmt.setString(1,member);
            check_stmt.setObject(2,location_chat);

            if(!check_stmt.executeQuery().next()) {
                stmt = conn.prepareStatement("INSERT INTO chat_member (chat_location, member) VALUES (?, ?)");
                stmt.setObject(1, location_chat);
                stmt.setString(2, member);

                Boolean rs = stmt.execute();

                stmt.close();

            }

            check_stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        DatabaseManager.setOutdated(true);
    }

    public void add_user(UserWeeat uw) {

        String username = uw.username;
        String email = uw.email;
        String image_url = uw.image_url;
        PreparedStatement stmt = null;
        PreparedStatement check_stmt = null;

        try {

            check_stmt = conn.prepareStatement("SELECT * FROM user_weeat WHERE  email = ? ;");
            check_stmt.setString(1,email);

            System.out.println("add_user size: "  + check_stmt.executeQuery().getFetchSize() );
            if(!check_stmt.executeQuery().next()) {
                stmt = conn.prepareStatement("INSERT INTO user_weeat (username,email,image_url) VALUES (?,?,?)");
                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, image_url);

                Boolean rs = stmt.execute();

                stmt.close();
            }

            check_stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        DatabaseManager.setOutdated(true);
    }

    public void add_message(MessageDB mdb) {

        String content = mdb.content;
        PGpoint chat_location = mdb.chat_location;
        String poster = mdb.poster;
        PreparedStatement stmt = null;
        PreparedStatement check_stmt = null;

        try {

            check_stmt = conn.prepareStatement("SELECT 1 FROM chat_member WHERE member = ? AND chat_location ~= ?;");
            check_stmt.setString(1,poster);
            check_stmt.setObject(2,chat_location);

            ResultSet rs1 = check_stmt.executeQuery();

            if(rs1.next()) {
                stmt = conn.prepareStatement("INSERT INTO message (content,chat_location,poster) VALUES (?,?,?);");
                stmt.setString(1, content);
                stmt.setObject(2, chat_location);
                stmt.setString(3, poster);

                Boolean rs = stmt.execute();

                stmt.close();
            } else {
                throw new Utils.UserIsNotMemberException();
            }

            rs1.close();
            check_stmt.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Utils.UserIsNotMemberException e) {
            System.out.println("User " + poster + " is not member of chat with location " + chat_location.toString());
        }

        DatabaseManager.setOutdated(true);
    }

    public void debug_users(){

        Statement stmt = null;
        try {

            stmt = conn.createStatement();

            System.out.println("*** USER_WEEAT ***");
            ResultSet rs = stmt.executeQuery("SELECT * FROM user_weeat;");
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

                PGpoint r_chat_location = (PGpoint)rs.getObject("chat_location");
                String member = rs.getString("member");
                ChatMember cm = new ChatMember(r_chat_location,member);
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
                PGpoint r_chat_location = (PGpoint)rs.getObject("chat_location");
                String poster = rs.getString("poster");
                String content = rs.getString("content");
                Timestamp date = rs.getTimestamp("date");

                MessageDB mdb = new MessageDB(id,date,content,r_chat_location,poster);
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

        DatabaseConnection db = new DatabaseConnection(false);

        db.close();
        DatabaseManager.database_delete();
        DatabaseManager.database_create();
        DatabaseManager.database_init();
        db.connect();

        System.out.println("Closing...");
        db.close();
    }
}