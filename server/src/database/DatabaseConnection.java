package src.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnection {

    public static void main(String args[]) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
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
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Records created successfully");
    }
}
