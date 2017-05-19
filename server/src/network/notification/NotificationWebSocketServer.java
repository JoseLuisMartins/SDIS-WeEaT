package network.notification;


import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;
import org.postgresql.geometric.PGpoint;

import java.util.ArrayList;
import java.util.HashMap;
import static network.GoogleLoginChecker.googleLoginChecker;


@WebSocket
public class NotificationWebSocketServer {

    private static HashMap<PGpoint,ArrayList<Session>> sessions = new HashMap<>();

    public static void main(String[] args) throws Exception {
        initWebSocket();
    }

    public static void initWebSocket() throws Exception {
        Server server = new Server();
        server.setHandler(new WebSocketHandler());


        //WS
        ServerConnector wsConnector = new ServerConnector(server);
        wsConnector.setHost("192.168.1.64");
        //wsConnector.setHost("172.30.26.247");
        wsConnector.setPort(8887);
        server.addConnector(wsConnector);

        //WSS
/*
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setTrustAll(true);
        sslContextFactory.setValidateCerts(false);
        //sslContextFactory.setKeyStorePath("keystore.jks");
        //sslContextFactory.setKeyStorePassword("OBF:1l1a1s3g1yf41xtv20731xtn1yf21s3m1kxs");

        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(8443);
        http_config.setOutputBufferSize(32768);
        http_config.setRequestHeaderSize(8192);
        http_config.setResponseHeaderSize(8192);
        http_config.setSendServerVersion(true);
        http_config.setSendDateHeader(false);

        HttpConfiguration https_config = new HttpConfiguration(http_config);
        https_config.addCustomizer(new SecureRequestCustomizer());

        ServerConnector wssConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),new HttpConnectionFactory(https_config));
        wssConnector.setHost("127.0.0.1");
        wssConnector.setPort(8443);
        server.addConnector(wssConnector);
*/
        server.start();
        server.join();

    }


    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Received Connection");
    }



    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("Message from Client: " + message);

        JSONObject obj = new JSONObject(message);

        PGpoint chatLocation = new PGpoint(obj.getDouble("lat"),obj.getDouble("long"));
        String token = obj.getString("token");

        JSONObject userInfo = googleLoginChecker(token);

        if(sessions.get(chatLocation) == null){
            ArrayList<Session> nArray = new ArrayList<>();
            nArray.add(session);
            sessions.put(chatLocation,nArray);
        }else {
            ArrayList<Session> array = sessions.get(chatLocation);
            array.add(session);
        }

    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("WebSocket Closed. Code:" + statusCode);
    }



    public static void sendAll(String msg, PGpoint chatLocation) {

        System.out.println("Sending notifications");

            try {
                ArrayList<Session> usersToNotify = sessions.get(chatLocation);

                if (usersToNotify == null)
                    return;

                ArrayList<Session > closedSessions= new ArrayList<>();

                for (Session session : usersToNotify) {
                    if(!session.isOpen()) {
                        System.err.println("|Closed session| policy: "+ session.getPolicy());
                        closedSessions.add(session);
                    }
                    else
                        session.getRemote().sendString(msg);
                }

                usersToNotify.removeAll(closedSessions);
                System.out.println("Sending "+msg+" to "+usersToNotify.size() + " clients on chat with location " + chatLocation);

            } catch (Throwable e) {
                e.printStackTrace();
            }
    }

}