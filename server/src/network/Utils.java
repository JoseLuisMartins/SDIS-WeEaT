package network;



import database.DatabaseConnection;

import javax.net.ssl.*;
import java.io.FileInputStream;
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






}
