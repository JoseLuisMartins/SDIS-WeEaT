package network;



import database.DatabaseConnection;
import network.notification.NotificationWebSocketServer;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.security.KeyStore;

public class Utils {

    public static final int SERVER_OPERATING = 0;
    public static final int SERVER_BACKUP = 1;
    public static GoogleLoginChecker google;
    public static SSLContext sslContext;
    public static DatabaseConnection db;

    public static void initDB(boolean restore){
        db = new DatabaseConnection(restore);
    }


    public static void init(String webSocketIP, int webSocketPort) {

        try {
            google = new GoogleLoginChecker();
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(new FileInputStream("src/keys/truststore"), "123456".toCharArray());


            //Init Notification WebSocket thread
            new Thread() {
                public void run() {
                    System.out.println("Running Static Utils");
                    try {
                        NotificationWebSocketServer.initWebSocket(webSocketIP ,webSocketPort);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static boolean isJsonValid(JSONObject obj, String... param){

        for (String s :param){
            if(!obj.has(s))
                return false;
        }

        return true;
    }

    public static class UserIsNotMemberException extends Throwable {
    }


}
