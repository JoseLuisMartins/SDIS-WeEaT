package network.load_balancer;

public class ServerPair {

    public static final int SERVER_OPERATING = 0;
    public static final int SERVER_BACKUP = 1;

    private ServerConnection operatingServer = null;
    private ServerConnection backupServer = null;

    public void removeServer(ServerConnection connection){

        System.out.println("REMOVING!! Connection");

        if(operatingServer.equals(connection)){
            if(backupServer != null) {
                backupServer.removeServerData("ROPE");
                operatingServer = backupServer;
                operatingServer.setMode(SERVER_OPERATING);
                backupServer = null;
            }else{
                operatingServer = null;
            }

        }else if(backupServer.equals(connection)){
            backupServer = null;
            if(operatingServer != null){
                operatingServer.removeServerData("RBAK");
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("OP[");
        if(operatingServer == null) {
            sb.append("NONE");
        }else {
            sb.append(operatingServer.getIP());
            sb.append(":");
            sb.append(operatingServer.getPort());
        }

        sb.append("] BACK[");
        if(backupServer == null){
            sb.append("NONE");
        }else {
            sb.append(backupServer.getIP());
            sb.append(":");
            sb.append(backupServer.getPort());
        }

        return sb.toString();
    }

    private void updateServerConnections(){
        operatingServer.setServerData("BACK", backupServer.getIP(), backupServer.getPort());
        backupServer.setServerData("OPER", operatingServer.getIP(), operatingServer.getPort());
    }

    public String getIP(){
        if(operatingServer == null)
            return null;

        return operatingServer.getIP().replace("/","");

    }

    public int getPort(){
        if(operatingServer == null)
            return -1;

        return operatingServer.getPort();
    }

    public int addServerConnection(ServerConnection con){
        System.out.println("TET" + this.toString());
        if(operatingServer == null) {
            operatingServer = con;
            operatingServer.setServerPair(this);
            if(backupServer!=null)
                updateServerConnections();
            
            return SERVER_OPERATING;
        }else if(backupServer == null){
            backupServer = con;
            backupServer.setServerPair(this);
            updateServerConnections();
            return SERVER_BACKUP;
        }

        return -1;
    }

}
