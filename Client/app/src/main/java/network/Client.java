package network;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.josemartins.sdis_weeat.R;
import network.messaging.Message;
import network.messaging.distributor.Distributor;
import network.messaging.distributor.client.ClientDistributor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class Client {


    private SSLContext sslContext;
    private Distributor distributor;



    public Client(Context context) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, UnrecoverableKeyException {
        initSSLContext(context);
        distributor = new ClientDistributor();
    }


    static {
        HttpsURLConnection.setDefaultHostnameVerifier(((hostname, session) -> true));

    }

    public void makeRequest(String url, String method, Message m) throws IOException {
        Request req = new Request();

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(m);

        req.execute(url.getBytes(),method.getBytes(),bout.toByteArray());
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


    class Request extends AsyncTask<byte[], Void, byte[]> {


        @Override
        protected byte[] doInBackground(byte[] ... params) {

            try {

                String urlString = new String(params[0]);
                String requestMethod = new String(params[1]);
                byte[] message = params[2];
                URL url = new URL(urlString);


                HttpsURLConnection con = (HttpsURLConnection)url.openConnection();


                try {
                    con.setSSLSocketFactory(sslContext.getSocketFactory());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                con.setRequestMethod(requestMethod);
                con.setDoInput(true);
                con.setDoOutput(true);

                con.getOutputStream().write(message);

                con.connect();

                System.out.println(con.getResponseCode());

                ObjectInputStream inputStream = new ObjectInputStream(con.getInputStream());


                Message messageReceived = null;
                try {
                    messageReceived = (Message)inputStream.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                distributor.distribute(messageReceived);

                inputStream.close();


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
        protected void onPostExecute(byte[] result) {
            Log.d("debug","Handle Response -> " + result);
        }

    }


}
