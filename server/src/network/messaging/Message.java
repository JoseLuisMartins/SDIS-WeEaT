package network.messaging;


import com.sun.net.httpserver.HttpExchange;
import network.messaging.distributor.Distributor;

import javax.net.ssl.*;
import javax.security.cert.CertificateException;
import java.io.*;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class Message implements Serializable{

    private int actionID;
    private Object content;
    private transient HttpExchange httpExchange;

    public Message(int actionID, Object content){
        this.httpExchange = null;
        this.actionID = actionID;
        this.content = content;
    }
    public Message(int actionID, Object content, HttpExchange httpExchange){
        this.actionID = actionID;
        this.content = content;
        this.httpExchange = httpExchange;
    }

    public int getClassID(){ return actionID;}

    public void setHttpExchange(HttpExchange e){
        httpExchange = e;
    }
    public Object getContent(){
        return content;
    }

    public HttpExchange getHttpExchange() {
        return httpExchange;
    }
    public static SSLContext getFactory() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, IOException, CertificateException, java.security.cert.CertificateException {

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

    public static void SendURLMessage(URL url, Message message, Distributor distributor) throws IOException, ClassNotFoundException {


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

        outputStream.writeObject(message);
        con.connect();

        System.out.println(con.getResponseCode());
        ObjectInputStream inputStream = new ObjectInputStream(con.getInputStream());

        Message messageReceived = null;
        messageReceived = (Message)inputStream.readObject();

        distributor.distribute(messageReceived);

        inputStream.close();
        outputStream.close();
    }

}
