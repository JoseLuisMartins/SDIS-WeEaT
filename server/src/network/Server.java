package network;


import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import network.messaging.Message;
import network.messaging.distributor.balancer.BalancerDistributor;
import network.messaging.distributor.server.ServerDistributor;
import network.sockets.SecureClient;
import org.json.JSONObject;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;

public class Server {

    private int mode = -1;
    private int port = 8888;
    private String ip = "";
    private ServerDistributor distributor = new ServerDistributor(this);
    private String request = "";
    public static void main(String args[]){

        try {
            Server s = new Server("127.0.0.1", 8000,8888);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setMode(int mode){
        System.out.println("Changed Server mode to:" + mode);
        this.mode = mode;
    }

    public Server(String loadBalancerIP, int loadBalancerPort, int port) throws Exception {
        request = loadBalancerIP;
       // new SecureClient("192.168.1.64",27015);
       /*
        URL balancer = new URL("https://"+loadBalancerIP+":" + loadBalancerPort);
        JSONObject object = new JSONObject();
        object.put("location", "Porto");
        object.put("port", port);
        Message.SendURLMessage(balancer, new Message(BalancerDistributor.STORE_SERVER, object.toString()) ,distributor);
*/

        HttpsServer server = getHttpsServer(port);
        server.createContext("/",new ServerHttpHandler());

        server.setExecutor(null);
        server.start();

    }

    public static HttpsServer getHttpsServer(int port) throws IOException, KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException {

        SSLContext serverContext = SSLContext.getInstance("TLS");

        //ponto 4.3
        //https://web.fe.up.pt/~pfs/aulas/sd2017/labs/l05/jsse_l05.html

        // initialise the keystore
        char[] password = "123456".toCharArray ();
        KeyStore keyStore = KeyStore.getInstance( "JKS" );
        FileInputStream fis = new FileInputStream ( "src/keys/server.keys" );
        keyStore.load( fis, password );

        // setup the key manager factory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance ( "SunX509" );
        kmf.init ( keyStore, password );


        serverContext.init(kmf.getKeyManagers(),null, null);

        //CREATE HTTPSSERVER
        HttpsServer server = HttpsServer.create(new InetSocketAddress(port), 0);
        server.setHttpsConfigurator(new HttpsConfigurator(serverContext));
        return server;
    }
}
