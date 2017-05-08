package com.example.josemartins.sdis_weeat.network;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.TrustManagerFactory;

public class Client {


    SSLContext sslContext;

    public Client(Context context) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, UnrecoverableKeyException {

        initSSLContext(context);
        Log.d("debug","Client-----");

        Request req = new Request();
        req.execute();
    }


    public void initSSLContext(Context context) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException {

        char[] password = "123456".toCharArray ();
        //client
        KeyStore keyStoreCient = KeyStore.getInstance ( "JKS" );
        keyStoreCient.load( context.getAssets().open("keys/client.keys"), password);

        // setup the key manager factory
        KeyManagerFactory kmfClient = KeyManagerFactory.getInstance ( KeyManagerFactory.getDefaultAlgorithm() );
        kmfClient.init ( keyStoreCient, password );


        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(context.getAssets().open("keys/truststore.bks"), password);

       // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(trustStore);

        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmfClient.getKeyManagers(), tmf.getTrustManagers(), null);

    }

    class Request extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {

            try {

                URL url = new URL("https://192.168.1.64:8000");

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
                urlConnection.setRequestMethod("GET");
                //urlConnection.connect();

                print_https_cert(urlConnection);
                print_content(urlConnection);

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

        private void print_https_cert(HttpsURLConnection con){

            if(con!=null){

                try {

                    Log.d("debug","Response Code : " + con.getResponseCode());
                    Log.d("debug","Cipher Suite : " + con.getCipherSuite());
                    Log.d("debug","\n");

                    Certificate[] certs = con.getServerCertificates();
                    for(Certificate cert : certs){
                        Log.d("debug","Cert Type : " + cert.getType());
                        Log.d("debug","Cert Hash Code : " + cert.hashCode());
                        Log.d("debug","Cert Public Key Algorithm : "
                                + cert.getPublicKey().getAlgorithm());
                        Log.d("debug","Cert Public Key Format : "
                                + cert.getPublicKey().getFormat());
                        Log.d("debug","\n");
                    }

                } catch (SSLPeerUnverifiedException e) {
                    e.printStackTrace();
                } catch (IOException e){
                    e.printStackTrace();
                }

            }

        }

        private void print_content(HttpsURLConnection con){
            if(con!=null){

                try {

                    Log.d("debug","****** Content of the URL ********");
                    BufferedReader br =
                            new BufferedReader(
                                    new InputStreamReader(con.getInputStream()));

                    String input;

                    while ((input = br.readLine()) != null){
                        Log.d("debug",input);
                    }
                    br.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }



        @Override
        protected void onPostExecute(Void result) {
            Log.d("debug","finish asyncTask-----");
        }

    }


}
