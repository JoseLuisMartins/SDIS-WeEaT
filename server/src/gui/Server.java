package gui;


import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyStore;

public class Server {

    public static void main(String [ ] args) throws Exception {

        System.out.println("WeEat Server");


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

        /*
        // setup the trust manager factory
        TrustManagerFactory tmf = TrustManagerFactory.getInstance ( "SunX509" );
        tmf.init ( keyStore );
        */

        serverContext.init(kmf.getKeyManagers(), null, null);


        //CREATE HTTPSSERVER
        HttpsServer server = HttpsServer.create(new InetSocketAddress(8080), 0);
        server.setHttpsConfigurator(new HttpsConfigurator(serverContext));
        server.setExecutor(null);
        server.start();


    }
}
