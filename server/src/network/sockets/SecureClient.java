package network.sockets;

import network.messaging.Message;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.security.KeyStore;

/**
 * Created by joao on 5/10/17.
 */
public class SecureClient extends Thread{

    private SSLSocket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public SecureClient(String ip, int port, int clientPort) throws Exception {

        SSLSocketFactory factory = getSSLServerSocketFactory("src/keys/client.keys","src/keys/truststore");

        socket = (SSLSocket) factory.createSocket();

        //ip/ port of the loadbalancer
        socket.connect(new InetSocketAddress(ip,port));

        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());

    }

    @Override
    public void run() {
        super.run();

        while (true){
            try {
                Message m = (Message) objectInputStream.readObject();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void sendData(Message m) throws IOException {
        objectOutputStream.writeObject(m);
        objectOutputStream.close();
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
