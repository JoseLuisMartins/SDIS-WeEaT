package network.load_balancer;

import network.sockets.ConnectionArmy;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by joao on 5/9/17.
 */
public class ServerConnection extends  Thread{

    private Socket socket;
    private String location;
    private ConnectionArmy army;
    private PrintWriter outputStream;
    private BufferedReader inputStream;
    private ServerPair pair;
    private int httpsPort = -1;


    /**
     * Trata de uma ligacao, com um servidor (operador ou backup)
     * Espera pelo handshake (confirmSocket()!)
     * @param s
     * @param army
     * @throws IOException
     */
    public ServerConnection(Socket s, ConnectionArmy army) throws IOException {
        this.socket = s;
        this.army = army;
        outputStream = new PrintWriter(s.getOutputStream(), true);
        inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String getLocation(){
        return  location;
    }

    public void setServerPair(ServerPair pair){
        this.pair = pair;
    }

    public boolean confirmSocket(){

        try {
            System.out.println(socket.getLocalPort() + "Waiting for confirmation Response");
            String response = inputStream.readLine();

            System.out.println("Received Confirmation"+ response);

            String confirmation = ConnectionArmy.getConfirmationCode();

            if(!response.startsWith(confirmation))
                return false;

            location = response.substring(confirmation.length(), response.length());

            String[] ports = inputStream.readLine().trim().split(" ");
            httpsPort = Integer.parseInt(ports[0]);


            if(!army.recruit(this))
                return false;

            System.out.println(army.toString());


        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    public void removeServerData(String cmd){
        outputStream.println(cmd);
    }

    public void setServerData(String cmd, String ip, int port){
        ip = ip.replace("/","");
        outputStream.println(cmd + ip + ":" + port);
    }

    public void setMode(int mode){
        outputStream.println("MODE" + ((mode == ServerPair.SERVER_BACKUP)? "BACKUP" : "OPERATOR"));
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(o instanceof ServerConnection){
            ServerConnection tmp = (ServerConnection)o;
            return tmp.getPort() == getPort() && tmp.getIP().equals(getIP());
        }
        return  false;
    }

    @Override
    public void run() {

        if(!confirmSocket()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        while(true){

            try {

                System.out.println("Waiting for" + getIP() + ":" + getPort());

                int byt = socket.getInputStream().read();

                if(socket.isClosed() || byt == -1 )
                    throw new IOException();

            } catch (IOException e) {
                System.out.println("ERROR Removing: " + getIP() + ":" + getPort());
                pair.removeServer(this);
                System.out.println(army);
                return;

            }
        }
    }

    public String getIP(){
        return ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress().toString();

    }

    public int getPort(){
        return socket.getPort();
    }

    public void sendData(byte[] msg){
        try {
            socket.getOutputStream().write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
