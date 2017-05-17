package network.sockets;

import network.load_balancer.ServerConnection;

import java.io.IOException;
import java.net.Socket;

public class SecureServerQuarters extends SecureServer {

    ConnectionArmy army;

    public SecureServerQuarters(int port, ConnectionArmy army) throws Exception {

        super(port);
        this.army=army;
    }

    @Override
    public void run() {

        while (true){
            try {
                System.out.println(serverSocket.getLocalPort() + " Waiting Connections");
                Socket s = serverSocket.accept();

                ServerConnection svConnection = new ServerConnection(s, army);

                System.out.println(serverSocket.getLocalPort() + " Creating Thread");

                svConnection.start();


            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
