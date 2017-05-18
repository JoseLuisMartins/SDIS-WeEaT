package network.notification;


import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class WebSocketHandler extends org.eclipse.jetty.websocket.server.WebSocketHandler {


    @Override
    public void configure(WebSocketServletFactory webSocketServletFactory) {
        webSocketServletFactory.register(NotificationWebSocketServer.class);
    }

}