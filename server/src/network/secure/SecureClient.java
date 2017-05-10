package network.secure;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.KeyStore;
import java.security.SecureRandom;

/**
 * Created by joao on 5/10/17.
 */
public class SecureClient {

    private SSLSocket socket;

    public SecureClient(String ip, int port) throws Exception {

        SSLSocketFactory factory = getSSLServerSocketFactory("src/keys/client.keys","src/keys/truststore");

        socket = (SSLSocket) factory.createSocket();

        socket.connect(new InetSocketAddress(ip,port));
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
