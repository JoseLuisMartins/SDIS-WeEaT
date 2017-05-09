package network.load_balancer;

/**
 * Created by joao on 5/9/17.
 */
public class ServerPair {

    public static final int SERVER_OPERATING = 0;
    public static final int SERVER_BACKUP = 1;

    private ServerConnection operatingServer = null;
    private ServerConnection backupServer = null;


    public int AddServerConnection(ServerConnection con){
        if(operatingServer == null) {
            operatingServer = con;
            return SERVER_OPERATING;
        }else if(backupServer == null){
            backupServer = con;
            return SERVER_BACKUP;
        }

        return -1;
    }

}
