package com.example.josemartins.sdis_weeat.gui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.josemartins.sdis_weeat.R;
import com.example.josemartins.sdis_weeat.logic.Utils;
import com.example.josemartins.sdis_weeat.network.Client;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    Context context;
    private SignInButton signIn;
    private GoogleApiClient googleApiClient;
    private static final int REQUEST_CODE = 9001;
    private TextView loginInfo;
    private static final String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = this;

        try {
            Utils.client = new Client(context);
        } catch (Exception e) {
            e.printStackTrace();
        }



        loginInfo= (TextView) findViewById(R.id.loginInfo);

        signIn = (SignInButton) findViewById(R.id.signInButton);
        signIn.setOnClickListener(signInListener);

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();


    }


    View.OnClickListener signInListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(i,REQUEST_CODE);
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
    

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }


    private void handleResult(GoogleSignInResult result){
        //loginInfo.setText(result.toString());
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String token =  account.getIdToken();
            loginInfo.setText("Name: " + name + "\nEmail: " + email + "\nToken: " + token);

            findViewById(R.id.signInButton).setVisibility(View.INVISIBLE);
            findViewById(R.id.logout).setVisibility(View.VISIBLE);

        }
    }


    public void logout(View v){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Log.d("debug","logout");
                findViewById(R.id.signInButton).setVisibility(View.VISIBLE);
                findViewById(R.id.logout).setVisibility(View.INVISIBLE);
                loginInfo.setText("");
            }
        });
    }

    public void request(View v){
        Utils.client.makeRequest("AuthUser","POST","Body".getBytes());

        Log.d("debug","activity-----");
        Intent i = new Intent(this,ChooseLocal.class);
        startActivity(i);
    }





}
