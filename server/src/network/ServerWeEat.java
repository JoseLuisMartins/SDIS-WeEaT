package network;


import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import network.messaging.distributor.server.ServerDistributor;
import network.protocol.SecureClientBermuda;
import network.protocol.SecureServerBermuda;
import network.sockets.SecureClientQuarters;
import sun.nio.ch.ThreadPool;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
    private HttpsServer server = null;

    public static void main(String args[]){


        try {

            /* Uncommented for stand alone server
            Utils.init();
            //Set true for restore
            Utils.initDB(false);
            */
            ServerWeEat s = new ServerWeEat("127.0.0.1", 8888,8001);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setMode(int mode) {

        System.out.println("Changed ServerWeEat mode to:" + mode);

        if(mode == this.mode)
            return;

        if(this.mode == -1){//First boot
            if(mode == Utils.SERVER_BACKUP){
                if(server != null){
                    server.stop(1);
                    if(server.getExecutor() != null)
                        ((ThreadPoolExecutor)server.getExecutor()).shutdown();
                    server = null;
                }
                if(ip_to_connect != null && client_bermuda == null) {
                    try {//Ligar o client para receber o backup da base de dados
                        client_bermuda = new SecureClientBermuda(ip_to_connect, port_to_connect);
                        client_bermuda.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {//SERVER OPERATOR
                if(server_bermuda == null){
                    start_operation(false);
                }
            }
        } else {
            if(mode == Utils.SERVER_OPERATING){
                ip_to_connect = null;
                if(client_bermuda != null)
                    client_bermuda.close();
                client_bermuda = null;
                start_operation(true);
            } else {//Switching to backup (never happens, just in case)
                if(server_bermuda != null)
                    server_bermuda.close();
                server_bermuda = null;
                setIp_confirmation(null);
            }
        }

        this.mode = mode;
    }

    private void start_operation(boolean restore) {

        try {
            server_bermuda = new SecureServerBermuda(port_to_connect);
            Utils.init();
            Utils.initDB(restore);

            if(server != null) {

                server = getHttpsServer(port);
                server.createContext("/", new ServerHttpHandler(this));

                server.setExecutor(null);
                server.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void shutdown(){
        System.out.println("Capacity for this location is full! Shuting Down!");
        System.exit(-1);
    }

    public ServerWeEat(String loadBalancerIP, int loadBalancerPort, int port) throws Exception {

        //Uncomment if you wish to use the awesome loadBalancer C;

        request = loadBalancerIP;
        SecureClientQuarters balancerClient =  new SecureClientQuarters("127.0.0.1",27015,port, "PORTO",this);
        balancerClient.start();

        //Thread.sleep(200000);
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

        if (server_bermuda != null)
            server_bermuda.start_sending_backups(ip_confirmation);
    }
}
