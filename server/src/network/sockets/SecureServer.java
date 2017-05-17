package network.sockets;

import network.load_balancer.ServerConnection;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;

/**
 * Created by joao on 5/10/17.
 */
public class SecureServer extends Thread{

    SSLServerSocketFactory factory;
    ServerSocket serverSocket;
    ConnectionArmy army;

    public SecureServer(int port, ConnectionArmy army) throws Exception {

        factory = getSSLServerSocketFactory("src/keys/server.keys","src/keys/truststore");
        this.army=army;

        serverSocket = factory.createServerSocket(port);
        System.out.println( port + " Accepting connections");

    }

    @Override
    public void run() {
        super.run();


        while (true){
            try {
                System.out.println(serverSocket.getLocalPort() + " Waiting Connections");
                Socket s = serverSocket.accept();

                ServerConnection svConnection = new ServerConnection(s, army);

                System.out.println(serverSocket.getLocalPort() + " Creating Thread");

                svConnection.start();


            } catch (IOException e) {
                e.printStackTrace();
                return;
            }


        }

    }

    public static SSLServerSocketFactory getSSLServerSocketFactory(String keyPath, String trustPath) throws Exception{

        char[] password = "123456".toCharArray();
        FileInputStream keyFile = new FileInputStream(keyPath);

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(keyFile,password);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);

        KeyManager keyManagers[] = keyManagerFactory.getKeyManagers();


        FileInputStream trustFile = new FileInputStream(trustPath);

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(trustFile,password);

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        TrustManager trustManagers[] = trustManagerFactory.getTrustManagers();


        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers , new SecureRandom());
        return sslContext.getServerSocketFactory();

    }

}
