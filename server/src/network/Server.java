package network;


import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import network.messaging.Message;
import network.messaging.ServerMessageParser;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
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
        KeyStore keyStore = KeyStore.getInstance( "JKS" );
        FileInputStream fis = new FileInputStream ( "src/keys/server.keys" );
        keyStore.load( fis, password );

        // setup the key manager factory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance ( "SunX509" );
        kmf.init ( keyStore, password );

/*
        KeyStore trustStore = KeyStore.getInstance("bks-v1");
        FileInputStream fi = new FileInputStream ( "src/keys/truststore.bks" );
        trustStore.load(fi, password);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(trustStore);*/



        serverContext.init(kmf.getKeyManagers(),null, null);

        //CREATE HTTPSSERVER
        HttpsServer server = HttpsServer.create(new InetSocketAddress(8000), 0);
        server.setHttpsConfigurator(new HttpsConfigurator(serverContext));

        server.createContext("/",new ServerHttpHandler());

        server.setExecutor(null);
        server.start();

        ServerMessageParser mp = new ServerMessageParser();

        mp.ReceiveMessage(new Message("WorkerAlert","Friend Hi!".getBytes()));


    }
}
