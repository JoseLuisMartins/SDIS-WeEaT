package network;

import com.sun.net.httpserver.HttpsServer;
import network.load_balancer.LoadBalancer;
import network.messaging.Message;
import network.messaging.distributor.balancer.BalancerDistributor;

import javax.net.ssl.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * Created by joao on 5/9/17.
 */
public class Client {


    public SSLContext getFactory() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, IOException, CertificateException {

        SSLContext sslContext;
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(new FileInputStream("src/keys/truststore") , "123456".toCharArray());

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext;
    }
    static {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
        {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }



    public Client(){

        try {
            URL url = new URL("https://192.168.1.97:8000");

            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            try {
                con.setSSLSocketFactory(getFactory().getSocketFactory());
            } catch (Exception e) {
                e.printStackTrace();
            }

            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);

            ObjectOutputStream outputStream = new ObjectOutputStream(con.getOutputStream());

            outputStream.writeObject(new Message(BalancerDistributor.REQUEST_SERVER, "Hello Ma Friend", null  ));

            con.connect();

            System.out.println(con.getResponseCode());
            ObjectInputStream inputStream = new ObjectInputStream(con.getInputStream());



            Message m = null;
            try {
                m = (Message)inputStream.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println(m.getContent());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] s){
        new Client();
    }


}
