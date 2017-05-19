package network.sockets;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.security.KeyStore;

public class SecureClient extends Thread {

    protected SSLSocket socket;
    protected PrintWriter writer;
    protected BufferedReader inputStream;

    public SecureClient(String ip, int port) throws Exception {


        SSLSocketFactory factory = getSSLServerSocketFactory("src/keys/client.keys","src/keys/truststore");

        socket = (SSLSocket) factory.createSocket();

        //ip/ port of the loadbalancer
        socket.connect(new InetSocketAddress(ip,port));
    }

    @Override
    public void run() {

    }

    public static SSLSocketFactory getSSLServerSocketFactory(String keyPath, String trustPath) throws Exception{

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
        sslContext.init(keyManagers, trustManagers , null);
        return sslContext.getSocketFactory();

    }

}