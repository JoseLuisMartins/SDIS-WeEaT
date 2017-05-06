package com.example.josemartins.sdis_weeat.network;


import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class Client {

    SSLContext sslContext;

    public Client(Context context) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {

        initSSLContext(context);


        URL url = new URL("localhost");
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
        urlConnection.setRequestMethod("GET");

        urlConnection.getOutputStream().write("request message".getBytes());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = urlConnection.getInputStream();

        int n;
        while ((n = is.read()) != -1) baos.write(n);
        is.close();


    }


    public void initSSLContext(Context context) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, KeyManagementException {
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(context.getAssets().open("keys/truststore.bks"), "123456".toCharArray());

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

    }
}
