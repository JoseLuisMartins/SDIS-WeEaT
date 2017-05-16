package network.sockets;

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


    public SecureServer(int port) throws Exception {

        factory = getSSLServerSocketFactory("src/keys/server.keys","src/keys/truststore");

        serverSocket = factory.createServerSocket(port);
        System.out.println("Accepting connections");
        serverSocket.accept();
        System.out.println("End;");
    }

    public void handleSocket(Socket s){

        new Thread( ()->  {
                   //   new ObjectOutputStream(s.getOutputStream())
            }
        ).start();

    }

    @Override
    public void run() {
        super.run();


        while (true){
            try {
                Socket s = serverSocket.accept();





            } catch (IOException e) {
                e.printStackTrace();
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
