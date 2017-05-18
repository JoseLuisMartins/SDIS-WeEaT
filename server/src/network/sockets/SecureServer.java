package network.sockets;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;


public class SecureServer extends Thread{

    protected SSLServerSocketFactory factory;
    protected ServerSocket serverSocket;

    public SecureServer(int port) throws Exception {

        factory = getSSLServerSocketFactory("src/keys/server.keys","src/keys/truststore");

        serverSocket = factory.createServerSocket(port);
        System.out.println( port + " Accepting connections");

    }

    @Override
    public void run() {

        while (true){
            try {

                System.out.println(serverSocket.getLocalPort() + " Waiting Connections");
                Socket s = serverSocket.accept();

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
