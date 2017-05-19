package network.sockets;

import java.io.*;
import java.util.HashMap;

public class SecureClientQuarters extends SecureClient{

    private static String confirmationCode = "Batata";
    private String location;

    private interface Action{
        void action(String msg);
    }

    private HashMap<String, Action> actions = new HashMap<>();

    /**
     * Conecta-se ao loadBalancer dado por ip,port, e faz o handshake (envia confirmationCode + localization,
     * espera por uma localizacao ou entao por falha de conexao
     * @param ip
     * @param port
     * @param clientPort
     * @param location
     * @throws Exception
     */

    public SecureClientQuarters(String ip, int port, int clientPort, String location) throws Exception {
        super(ip,port,clientPort);
        init();
        this.location = location;

        handShake();
        this.start();

    }

    private void init(){
        actions.put("MODE", m -> changeMode(m));
        actions.put("BACK", m -> setBackupServer(m));
        actions.put("OPER", m -> setOperatorServer(m));
        actions.put("RBAC", m -> removeBackupServer(m));
        actions.put("ROPE", m -> removeOperatorServer(m));
    }

    public void handShake() throws IOException {

        writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println(confirmationCode + location);
        inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String res = inputStream.readLine();
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
                System.out.println("SecureClientQuarters: Waiting For MSG!");
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

    }

    private void setBackupServer(String ipPort){
        StringBuilder ip = new StringBuilder();
        int port = parseIPPort(ipPort, ip);
        if(port == -1)
            return;
        System.out.println("BackupServer data [" + ip.toString() + " " + port );

    }

    private void changeMode(String mode){
        System.out.println("ModeChanged " + mode );
    }

    private void removeBackupServer(String msg){
        System.out.println("Removing Backup, Disconnected");
    }

    private void removeOperatorServer(String msg){
        System.out.println("Removing Operator, Disconnected");
    }
}
