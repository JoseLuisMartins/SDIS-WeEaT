package network.protocol;

import network.sockets.SecureClient;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class SecureClientBermuda extends SecureClient {

    public static int file_chunk_size = 640000000;
    public static int server_port = 65464;
    public static String server_ip = "localhost";
    public static String file_path = "." + File.separator + "received.backup";

    public SecureClientBermuda(String ip, int port, int clientPort) throws Exception {
        super(ip, port, clientPort);
    }

    @Override
    public void run() {
        receive_database_backup();
    }

    public void receive_database_backup(){

        System.out.println("Receiving backup data!");

        int bytesRead;
        int current = 0;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        Socket socket = null;
        try {
            socket = new Socket(server_ip, server_port);
            System.out.println("Connecting...");

            // receive file
            byte [] mybytearray  = new byte [file_chunk_size];
            InputStream is = socket.getInputStream();
            fos = new FileOutputStream(file_path);
            bos = new BufferedOutputStream(fos);
            bytesRead = is.read(mybytearray,0,mybytearray.length);
            current = bytesRead;

            do {
                bytesRead =
                        is.read(mybytearray, current, (mybytearray.length-current));
                if(bytesRead >= 0) current += bytesRead;
            } while(bytesRead > -1);

            bos.write(mybytearray, 0 , current);
            bos.flush();
            System.out.println("File " + file_path
                    + " downloaded (" + current + " bytes read)");
            fos.close();
            bos.close();
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        receive_database_backup();
    }
}
