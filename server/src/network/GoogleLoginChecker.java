package network;


import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONObject;


import javax.net.ssl.HttpsURLConnection;

import static network.Utils.readAll;

public class GoogleLoginChecker {

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

                //System.out.println(obj.toString());


                if (obj.has("error_description"))
                    return null;

                return obj;
            }catch (Exception e ){
                //e.printStackTrace();
                return null;
            }

    }

}