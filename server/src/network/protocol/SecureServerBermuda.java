package network.protocol;

import database.DatabaseManager;
import network.Utils;
import network.sockets.SecureServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class SecureServerBermuda extends SecureServer {

    private static String backup_file = "." + File.separator + "db.backup";
    private static int interval = 15;
    private Socket socket = null;
    private Timer timer = null;
    public SecureServerBermuda(int port) throws Exception {
        super(port);
        this.start();
    }

    public void close(){

        try {

            timer.cancel();
            timer.purge();
            socket.close();
            serverSocket.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            try {
                socket = serverSocket.accept();

                while(true){

                    try {
                        int v = socket.getInputStream().read();

                        if (v == -1)
                            throw new IOException();

                    } catch (IOException e){
                        socket = null;
                        break;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start_sending_backups(String ip_confirmation) {

        System.out.println("Starting to send backup file to backup server");

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                if(socket == null)
                    return;

                System.out.println("Sending backup file!");
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                DataOutputStream dataOutputStream = null;
                System.out.println("Waiting...");
                try {

                    if(!socket.getInetAddress().toString().replace("/","").equals(ip_confirmation)){
                        System.out.println("Connection unsecure from " + socket.getInetAddress().toString());
                        System.out.println("Expected " + ip_confirmation);
                        socket.close();
                        return;
                    }

                    System.out.println("Accepted connection : " + socket);
                    // send file
                    File myFile = new File(backup_file);

                    byte[] mybytearray = new byte[(int) myFile.length()];
                    fis = new FileInputStream(myFile);
                    bis = new BufferedInputStream(fis);
                    bis.read(mybytearray, 0, mybytearray.length);
                    System.out.println("Sending " + backup_file + "(" + mybytearray.length + " bytes)");
                    if(dataOutputStream == null)
                        dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    dataOutputStream.writeInt(mybytearray.length);
                    dataOutputStream.write(mybytearray, 0, mybytearray.length);

                    System.out.println("Done.");
                } catch (IOException e) {
                    try {
                        fis.close();
                        bis.close();
                        socket.close();
                        socket=null;
                        this.cancel();

                    } catch (IOException e1) {
                        e1.printStackTrace();
                        return;
                    }
                    e.printStackTrace();
                    return;
                }
            }
        }, 0, interval * 1000);
    }
}
