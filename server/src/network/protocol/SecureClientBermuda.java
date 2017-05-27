package network.protocol;

import network.sockets.SecureClient;

import java.io.*;
import java.net.Socket;

public class SecureClientBermuda extends SecureClient {

    public static String file_path = "." + File.separator + "received.backup";
    private boolean run = true;

    public SecureClientBermuda(String ip, int port) throws Exception {
        super(ip, port);
    }

    @Override
    public void run() {
        receive_database_backup();
    }

    public void close() {
        try {
            socket.close();
            run = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receive_database_backup(){


        DataInputStream dataIn = null;
        try {
            dataIn = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(run) {
            System.out.println("Receiving backup data!");

            int bytesRead;
            int current = 0;
            FileOutputStream fos = null;

            try {

                System.out.println("Connecting...");

                // receive file
                InputStream in = socket.getInputStream();
                int file_size = dataIn.readInt();
               System.out.println("File Size is : " + file_size);

                System.out.println("Connected!");

                if(file_size  < 0 )
                    continue;

                byte[] byte_array = new byte[file_size];
                // Writing the file to disk
                // Instantiating a new output stream object
                FileOutputStream output = new FileOutputStream(file_path, false);
                output.flush();


                int count = 0;
                do {
                    bytesRead = dataIn.read(byte_array);

                    if(bytesRead == -1)
                        break;

                    System.out.println("Count : " + count + " | Bytes Read : " + bytesRead);
                    output.write(byte_array, count, bytesRead);
                    count += bytesRead;
                } while (count < file_size);
                output.close();

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        try {
            dataIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
