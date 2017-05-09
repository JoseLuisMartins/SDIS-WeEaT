package com.example.josemartins.sdis_weeat.network;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.josemartins.sdis_weeat.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public class Client implements Serializable {


    SSLContext sslContext;
    String loginHash;

    public Client(Context context) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, UnrecoverableKeyException {
        initSSLContext(context);
    }

    public Client setLoginHash(String loginHash) {
        this.loginHash = loginHash;
        return this;
    }

    static {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
        {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }

    public void makeRequest(String requestType,String method,byte[] body){
        Request req = new Request();
        req.execute(requestType.getBytes(),method.getBytes(),body);
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

    class Request extends AsyncTask<byte[], Void, String> {


        @Override
        protected String doInBackground(byte[] ... params) {

            try {

                String requestType = new String(params[0]);
                String requestMethod = new String(params[1]);
                byte[] body = params[2];

                URL url = new URL("https://192.168.1.64:8000/" + requestType);


                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

                urlConnection.setRequestMethod(requestMethod);
                urlConnection.getOutputStream().write(body);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int responseCode = urlConnection.getResponseCode();

                InputStream is;
                if (200 <= responseCode && responseCode <= 299) {
                    is = urlConnection.getInputStream();
                } else {
                    is = urlConnection.getErrorStream();
                }

                int n;
                while ((n = is.read()) != -1) baos.write(n);
                is.close();


                Log.d("debug","asyncTasktest-> " +  new String(baos.toByteArray()));
                Log.d("debug","asyncTask-----> " + String.valueOf(urlConnection.getResponseCode()) + " " + urlConnection.getResponseMessage() + "\r\n" );

                String res = new String(baos.toByteArray());
                return res;

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
        protected void onPostExecute(String result) {
            Log.d("debug","Handle Response -> " + result);
        }

    }


}
