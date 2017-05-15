package network;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

public class GoogleLoginChecker {

    private final GoogleIdTokenVerifier mVerifier;
    private final JsonFactory mJFactory;
    private String mProblem = "Verification failed. (Time-out?)";

    public GoogleLoginChecker() {

        NetHttpTransport transport = new NetHttpTransport();
        mJFactory = new GsonFactory();
        mVerifier = new GoogleIdTokenVerifier.Builder(transport, mJFactory).setAudience(Collections.singletonList("1077664049472-28kaormv1qla422j0jjkr2e6j5icbqke.apps.googleusercontent.com")).build();
    }

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
    }
}