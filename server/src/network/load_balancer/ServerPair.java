package network.load_balancer;

public class ServerPair {

    public static final int SERVER_OPERATING = 0;
    public static final int SERVER_BACKUP = 1;

    private ServerConnection operatingServer = null;
    private ServerConnection backupServer = null;

    public void removeServer(ServerConnection connection){

        System.out.println("REMOVING!! I'm Stupid");

        if(operatingServer.equals(connection)){

            operatingServer = backupServer;
            backupServer = null;

        }else if(backupServer.equals(connection)){
            backupServer = null;
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

    public int AddServerConnection(ServerConnection con){
        System.out.println("TET" + this.toString());
        if(operatingServer == null) {
            operatingServer = con;
            operatingServer.setServerPair(this);
            return SERVER_OPERATING;
        }else if(backupServer == null){
            backupServer = con;
            backupServer.setServerPair(this);
            return SERVER_BACKUP;
        }

        return -1;
    }

}
