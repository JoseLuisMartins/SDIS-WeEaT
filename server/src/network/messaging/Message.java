package network.messaging;


import com.sun.net.httpserver.HttpExchange;
import network.Utils;
import network.messaging.distributor.Distributor;
import org.json.JSONObject;

import javax.net.ssl.*;
import javax.security.cert.CertificateException;
import java.io.*;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class Message implements Serializable{

    private static final long serialVersionUID = 1L;

    private int actionID;
    private Object content;
    private transient HttpExchange httpExchange;
    private transient JSONObject userInfo;

    public Message(int actionID, Object content, JSONObject userInfo){
        this.httpExchange = null;
        this.actionID = actionID;
        this.content = content;
        this.userInfo = userInfo;
    }

    public Message(int actionID, Object content){
        this.httpExchange = null;
        this.actionID = actionID;
        this.content = content;
    }


    public JSONObject getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(JSONObject userInfo) {
        this.userInfo = userInfo;
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

    public static void sendURLMessage(URL url, Message message, Distributor distributor) throws IOException, ClassNotFoundException {


        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

        con.setHostnameVerifier((hostname, session) -> true);

        try {
            con.setSSLSocketFactory(Utils.sslContext.getSocketFactory());
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


        Message messageReceived = (Message)inputStream.readObject();

        distributor.distribute(messageReceived);

        inputStream.close();
        outputStream.close();
    }

}
