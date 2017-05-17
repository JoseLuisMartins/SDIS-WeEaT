package network;


import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import java.util.ArrayList;
import java.util.List;

public class WebSocketServer extends WebSocketHandler {


    public static void main(String[] args) throws Exception {


        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setTrustAll(true);
        //sslContextFactory.setKeyStorePath("keystore.jks");
        //sslContextFactory.setKeyStorePassword("OBF:1l1a1s3g1yf41xtv20731xtn1yf21s3m1kxs");

        Server server = new Server();
        server.setHandler(new WebSocketServer());

        //WS
        ServerConnector wsConnector = new ServerConnector(server);
        wsConnector.setHost("127.0.0.1");
        wsConnector.setPort(8080);
        server.addConnector(wsConnector);



        //WSS

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

        server.start();
        server.join();

    }


    @Override
    public void configure(WebSocketServletFactory webSocketServletFactory) {
        webSocketServletFactory.register(MyWebSocket.class);
    }

}