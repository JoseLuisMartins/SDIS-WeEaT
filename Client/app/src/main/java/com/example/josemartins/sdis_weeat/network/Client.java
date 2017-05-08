package com.example.josemartins.sdis_weeat.network;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.josemartins.sdis_weeat.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class Client {


    SSLContext sslContext;

    public Client(Context context) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, UnrecoverableKeyException {
        Log.d("debug","0Client-----");
        initSSLContext(context);
        Log.d("debug","Client-----");

        //make request
        Request req = new Request();
        req.execute();
    }

    static {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
        {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }


    public void initSSLContext(Context context) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException {

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(context.getResources().openRawResource(R.raw.truststore), "123456".toCharArray());

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
    }

    class Request extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {

            try {

                URL url = new URL("https://192.168.1.64:8000");


                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());


                /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream is = urlConnection.getInputStream();
                int n;
                while ((n = is.read()) != -1) baos.write(n);
                is.close();
*/

                //urlConnection.connect();

              //  Log.d("debug","asyncTasktest-> " + baos.toByteArray());
                Log.d("debug","asyncTask-----> " + String.valueOf(urlConnection.getResponseCode()) + " " + urlConnection.getResponseMessage() + "\r\n" );

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            Log.d("debug","finish asyncTask-----");
        }

    }


}
