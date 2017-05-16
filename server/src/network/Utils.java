package network;



import database.DatabaseConnection;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.security.KeyStore;

public class Utils {

    public static GoogleLoginChecker google;
    public static SSLContext sslContext;
    public static DatabaseConnection db;

    public static void initDB(){
        db = new DatabaseConnection();
    }


    static {

        try {
            google = new GoogleLoginChecker();
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(new FileInputStream("src/keys/truststore"), "123456".toCharArray());

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

        }catch (Exception e){
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


    public static class UserIsNotMemberException extends Throwable {
    }


}
