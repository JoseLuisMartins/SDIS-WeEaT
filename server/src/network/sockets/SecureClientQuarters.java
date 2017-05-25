package network.sockets;

import network.ServerWeEat;
import network.Utils;

import java.io.*;
import java.util.HashMap;

public class SecureClientQuarters extends SecureClient{

    private static String confirmationCode = "Confirmed";
    private String location;
    private int webSocketPort;
    private String webSocketIP;
    private ServerWeEat server = null;
    private int httpsPort = 0;
    private int backupPort = 0;


    private interface Action{
        void action(String msg);
    }

    private HashMap<String, Action> actions = new HashMap<>();


    /**
     * Conecta-se ao loadBalancer dado por ip,port, e faz o handshake (envia confirmationCode + localization,
     * espera por uma localizacao ou entao por falha de conexao
     * @param ip
     * @param port
     * @param location
     * @throws Exception
     */

    public SecureClientQuarters(String ip, int port, int httpsPort, String location, ServerWeEat server, String webSocketIP ,int webSocketPort, int backupPort) throws Exception {
        super(ip,port);
        init();
        this.location = location;
        this.server = server;
        this.httpsPort = httpsPort;
        this.webSocketIP = webSocketIP;
        this.webSocketPort = webSocketPort;
        this.backupPort = backupPort;

        handShake();

    }

    private void init(){
        actions.put("MODE", m -> changeMode(m));
        actions.put("BACK", m -> setBackupServer(m));
        actions.put("OPER", m -> setOperatorServer(m));
        actions.put("RBAC", m -> removeBackupServer(m));
        actions.put("ROPE", m -> removeOperatorServer(m));
    }

    public void handShake() throws IOException {

        System.out.println("Starting Handshake!");
        writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println(confirmationCode + location);
        writer.print(this.httpsPort);
        writer.print(" " + this.webSocketIP);
        writer.print(" " + this.webSocketPort);
        writer.println(" " + this.backupPort);
        inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String res = inputStream.readLine();
        if(res == null)
            res = "MODEFULL";
        dispatch(res);
    }

    private void dispatch(String msg){
        String cmd = msg.substring(0,4);
        System.out.println("DISPATCHER" + msg);
        if(actions.containsKey(cmd)){
            actions.get(cmd).action(msg.substring(4,msg.length()));
        }else{
            System.out.println("SecureClientQuarters: Command unknown" + cmd + " FROM " + msg);
        }
    }

    @Override
    public void run() {

        while (true){
            try{
                System.out.println("SecureClientQuarters: Waiting For MSG!\n");
                String msg = inputStream.readLine();
                if(msg == null)
                    throw new Exception();
                dispatch(msg);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private int parseIPPort(String ipPort, StringBuilder res){
        String[] parts = ipPort.split(":");
        if(parts.length != 2)
            return -1;

        res.append(parts[0]);

        int port = Integer.parseInt(parts[1]);
        return port;
    }

    private void setOperatorServer(String ipPort){
        StringBuilder ip = new StringBuilder();
        int port = parseIPPort(ipPort, ip);
        if(port == -1)
            return;
        System.out.println("OperatorServer data [" + ip.toString() + " " + port );

        server.set_backup_port_to_connect(port);
        server.setIp_to_connect(ip.toString());
    }



    private void setBackupServer(String ipPort){
        StringBuilder ip = new StringBuilder();
        int port = parseIPPort(ipPort, ip);
        if(port == -1)
            return;
        System.out.println("BackupServer data [" + ip.toString() + " " + port );

        server.setIp_confirmation(ip.toString());

    }

    private void changeMode(String mode){
        System.out.println("ModeChanged " + mode );
        if(mode.equals("BACKUP")){
            server.setMode(Utils.SERVER_BACKUP);
        } else if(mode.equals("OPERATOR")){
            server.setMode(Utils.SERVER_OPERATING);
        } else if(mode.equals("FULL")){
            server.shutdown();
        } else {
            System.out.println("Unrecognized Mode!");
        }
    }

    private void removeBackupServer(String msg){
        System.out.println("Removing Backup, Disconnected");
        server.setIp_confirmation(null);
        server.setServer_bermuda(null);
    }

    private void removeOperatorServer(String msg){
        System.out.println("Removing Operator, Disconnected");
        server.setIp_to_connect(null);
        server.setClient_bermuda(null);
    }
}
