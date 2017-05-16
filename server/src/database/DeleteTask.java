package database;

import network.Utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteTask implements Runnable {

    @Override
    public void run() {

        synchronized (Utils.db){

            PreparedStatement stmt = null;
            try {
                stmt = Utils.db.conn.prepareStatement("DELETE FROM messages;");
                stmt.execute();

                stmt = Utils.db.conn.prepareStatement("DELETE FROM chat_member;");
                stmt.execute();

                stmt = Utils.db.conn.prepareStatement("DELETE FROM chatroom;");
                stmt.execute();

                Utils.db.conn.commit();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
