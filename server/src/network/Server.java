package network;


import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;

public class Server {


    public Server() throws Exception {

        //SSL CONTEXT

        SSLContext serverContext = SSLContext.getInstance("TLS");

        //ponto 4.3
        //https://web.fe.up.pt/~pfs/aulas/sd2017/labs/l05/jsse_l05.html

        // initialise the keystore
        char[] password = "123456".toCharArray ();
        KeyStore keyStore = KeyStore.getInstance ( "JKS" );
        FileInputStream fis = new FileInputStream ( "src/keys/server.keys" );
        keyStore.load ( fis, password );

        // setup the key manager factory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance ( "SunX509" );
        kmf.init ( keyStore, password );



        serverContext.init(kmf.getKeyManagers(), null, null);

        //CREATE HTTPSSERVER
        HttpsServer server = HttpsServer.create(new InetSocketAddress(8000), 0);
        server.setHttpsConfigurator(new HttpsConfigurator(serverContext));

        server.createContext("/",new ServerHttpHandler());

        server.setExecutor(null);
        server.start();


    }
}