package network;


import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import network.messaging.distributor.server.ServerDistributor;
import network.protocol.SecureClientBermuda;
import network.protocol.SecureServerBermuda;
import network.sockets.SecureClientQuarters;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;

public class ServerWeEat {

    private int mode = -1;
    private int port = 8888;
    private String ip = "";
    private ServerDistributor distributor = new ServerDistributor(this);
    private String request = "";
    private SecureClientBermuda client_bermuda = null;
    private SecureServerBermuda server_bermuda = null;
    private String ip_to_connect = null;
    private static final int port_to_connect = 27000;

    public static void main(String args[]){


        try {
            //Uncomment if you wish to use the awesome loadBalancer C;

            //Set true for restore
            Utils.initDB(false);
            ServerWeEat s = new ServerWeEat("127.0.0.1", 8888,8000);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setMode(int mode){

        System.out.println("Changed ServerWeEat mode to:" + mode);

        if(mode == this.mode)
            return;

        if(this.mode == -1){//First boot
            if(mode == Utils.SERVER_BACKUP){
                if(ip_to_connect != null && client_bermuda == null) {
                    try {//Ligar o client para receber o backup da base de dados
                        client_bermuda = new SecureClientBermuda(ip_to_connect, port_to_connect);
                        client_bermuda.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {//SERVER OPERATOR
                if(server_bermuda == null){
                    try {
                        server_bermuda = new SecureServerBermuda(port_to_connect);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            if(mode == Utils.SERVER_OPERATING){

            } else {

            }
        }

        this.mode = mode;
    }

    public ServerWeEat(String loadBalancerIP, int loadBalancerPort, int port) throws Exception {

/*
        request = loadBalancerIP;
        SecureClientQuarters balancerClient =  new SecureClientQuarters("127.0.0.1",27015, "PORTO",this);

        if(this.mode == -1) {
            System.out.println("Capacity for this location is full! Shuting Down!");
            System.exit(-1);
        }

        Thread.sleep(200000);
*/
        HttpsServer server = getHttpsServer(port);
        server.createContext("/",new ServerHttpHandler(this));

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

    public void setClient_bermuda(SecureClientBermuda client_bermuda) {
        this.client_bermuda = client_bermuda;
    }

    public void setServer_bermuda(SecureServerBermuda server_bermuda) {
        this.server_bermuda = server_bermuda;
    }

    public void setIp_to_connect(String ip_to_connect) {
        this.ip_to_connect = ip_to_connect;
    }

    public void setIp_confirmation(String ip_confirmation) {

        if(server_bermuda != null){
            server_bermuda.start_sending_backups(ip_confirmation);
        } else {
            server_bermuda.setIp_confirmation(ip_confirmation);
        }

    }
}
