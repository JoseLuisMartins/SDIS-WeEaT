package network;


import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import database.DatabaseManager;
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
import java.util.concurrent.ThreadPoolExecutor;

public class ServerWeEat {

    private int mode = -1;
    private int port;
    private String ip = "";
    private ServerDistributor distributor = new ServerDistributor(this);
    private String request = "";
    private SecureClientBermuda client_bermuda = null;
    private SecureServerBermuda server_bermuda = null;
    private String ip_to_connect = null;
    private int backupPort;
    private int backupToConnectPort;
    private HttpsServer server = null;
    private int webSocketPort;
    private String webSocketIP;

    public static void main(String args[]){

        if(args.length != 8){
            System.out.print("USAGE: \n \t <locationString> <serverIp> <serverPort> <balancerIp> <balancerPort> <WebSocketPort> <backupPort> <path_to_pgsql_bin>\n");
            return;
        }

        String locationString= args[0];
        String serverIp= args[1];
        int serverPort= Integer.parseInt(args[2]);
        String balancerIp= args[3];
        int balancerPort= Integer.parseInt(args[4]);
        int webSocketPort= Integer.parseInt(args[5]);
        int backup_to_connect= Integer.parseInt(args[6]);
        DatabaseManager.bin_path = args[7];

        try {

            ServerWeEat s = new ServerWeEat(balancerIp, balancerPort,serverIp,serverPort,locationString,webSocketPort,backup_to_connect);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public ServerWeEat(String loadBalancerIP, int loadBalancerPort, String serverIP, int port, String locationString,int webSocketPort, int backupPort) throws Exception {

        //Uncomment if you wish to use the awesome loadBalancer C;
        this.port = port;
        this.request = loadBalancerIP;
        this.webSocketPort = webSocketPort;
        this.webSocketIP = serverIP;
        this.backupPort = backupPort;

        SecureClientQuarters balancerClient =  new SecureClientQuarters(loadBalancerIP,loadBalancerPort, port, locationString, this, serverIP, webSocketPort, backupPort);
        balancerClient.start();

    }

    public void set_backup_port_to_connect(int backup_port_to_connect){
        this.backupToConnectPort = backup_port_to_connect;
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
                        client_bermuda = new SecureClientBermuda(ip_to_connect, backupToConnectPort);
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
            server_bermuda = new SecureServerBermuda(backupPort);
            Utils.init(this.webSocketIP, this.webSocketPort);
            Utils.initDB(restore);

            if(server == null) {

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
