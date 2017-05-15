package network;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.json.JSONObject;


import javax.net.ssl.HttpsURLConnection;

import static network.Utils.readAll;

public class GoogleLoginChecker {
/*
    private final GoogleIdTokenVerifier mVerifier;
    private final JsonFactory mJFactory;
    private String mProblem = "Verification failed. (Time-out?)";*/

    public static JSONObject googleLoginChecker(String token) {


            try {

                StringBuilder bob_o_construtor = new StringBuilder();
                bob_o_construtor.append("https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=");
                bob_o_construtor.append(token);
                URL url = new URL(bob_o_construtor.toString());


                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                con.setRequestProperty("token", token);

                con.setRequestMethod("GET");

                con.connect();
                con.getResponseCode();

                BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName("UTF-8")));
                JSONObject obj = new JSONObject(readAll(rd));

                System.out.println(obj.toString());


                if (obj.has("error_description"))
                    return null;

                return obj;
            }catch (Exception e ){
              //  e.printStackTrace();
                return null;
            }



        /*
        NetHttpTransport transport = new NetHttpTransport();
        mJFactory = new GsonFactory();
        mVerifier = new GoogleIdTokenVerifier.Builder(transport, mJFactory).setAudience(Collections.singletonList("1077664049472-28kaormv1qla422j0jjkr2e6j5icbqke.apps.googleusercontent.com")).build();
        */
    }



    /*

    public GoogleIdToken.Payload check(String tokenString) {
        GoogleIdToken.Payload payload = null;

        try {
            System.out.println("oi");
            GoogleIdToken idToken = mVerifier.verify(tokenString);
            System.out.println("oi1");

            if (idToken != null)
                payload = idToken.getPayload();


        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return payload;
    }


    public String problem() {
        return mProblem;
    }*/
}