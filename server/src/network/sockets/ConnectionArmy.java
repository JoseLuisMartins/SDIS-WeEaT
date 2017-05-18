package network.sockets;

import network.load_balancer.ServerConnection;
import network.load_balancer.ServerPair;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


public class ConnectionArmy {

    int sameIPConnectionsNum = 1;
    int basePort = 27015;

    private static String confirmationCode = "Batata";

    private ConcurrentHashMap<String, ServerPair> servers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String,String> ipToLocation = new ConcurrentHashMap<>();

    private ArrayList<SecureServerQuarters> listeners = new ArrayList<>();

    public static String getConfirmationCode(){
        return confirmationCode;
    }

    public ConnectionArmy(int basePort, int sameIPConnectionsNum) throws Exception {
        this.basePort = basePort;
        this.sameIPConnectionsNum = sameIPConnectionsNum;
        startListenServers();
    }

    private void startListenServers() throws Exception {

        for(int i = 0; i < sameIPConnectionsNum; i++){
            SecureServerQuarters sv = new SecureServerQuarters(basePort + i, this);
            sv.start();
            listeners.add(sv);
        }
    }
    //Se conseguir adicionar o socket, fica recrutado, e envia-se confirmacao,
    // em caso contrario, retorna falso (vai fechar a ligacao)
    //
    public boolean recruit(ServerConnection connection){
        ServerPair pair;
        if(servers.containsKey(connection.getLocation())){
            pair = servers.get(connection.getLocation());
        }else{
            pair = new ServerPair();
            servers.put(connection.getLocation(), pair);
        }


        int mode = pair.AddServerConnection(connection);
        if(mode == -1)
            return false;

        ipToLocation.put(connection.getIP()+":"+connection.getPort(), connection.getLocation());
        connection.setMode(mode);
        return  true;

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        servers.forEach((k,v) -> {
            builder.append("Location:");
            builder.append(k);
            builder.append(" ::. ");
            builder.append(v);
            builder.append("\n");
        });

        return builder.toString();
    }
}
