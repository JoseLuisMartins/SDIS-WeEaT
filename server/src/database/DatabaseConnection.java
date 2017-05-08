package src.database;

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

    public void get_chatrooms() {

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM chatroom;" );
            while ( rs.next() ) {
                int id = rs.getInt("id");
                Timestamp date = rs.getTimestamp("date");
                Point location = (Point)rs.getObject("location");
                String creator = rs.getString("creator");

                System.out.println( "Id = " + id);
                System.out.println( "Date = " + date.toString());
                System.out.println( "location = " + location.toString());
                System.out.println( "creator = " + creator);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void add_chatroom(Point location, Timestamp date,String creator) {

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "INSERT INTO chatroom (location,creator,date);" );

            rs.close();
            stmt.close();
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

        DatabaseConnection db = new DatabaseConnection();

        db.test();
        DatabaseManager.database_backup();

        db.close();
        DatabaseManager.database_delete();
        DatabaseManager.database_create();
        db.connect();
        DatabaseManager.database_restore();

        db.print();

        System.out.println("Closing...");
        db.close();

    }
}

/*Class.forName("org.postgresql.Driver");
c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/weeat","postgres", "im80re93");
c.setAutoCommit(false);
System.out.println("Opened database successfully");

stmt = c.createStatement();
String sql = "INSERT INTO user_weeat (username) "
        + "VALUES ('Alberto Joao');";
stmt.executeUpdate(sql);

sql = "INSERT INTO user_weeat (username) "
        + "VALUES ('Felismino');";
stmt.executeUpdate(sql);

sql = "INSERT INTO user_weeat (username) "
        + "VALUES ('Botato Suporifero');";
stmt.executeUpdate(sql);

stmt = c.createStatement();
ResultSet rs = stmt.executeQuery( "SELECT * FROM user_weeat;" );
while ( rs.next() ) {
    String username = rs.getString("username");

    System.out.println( "Username = " + username );
}

rs.close();
stmt.close();
c.close();
*/