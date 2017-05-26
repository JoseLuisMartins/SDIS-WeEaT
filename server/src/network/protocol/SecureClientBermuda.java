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

        while(run) {
            System.out.println("Receiving backup data!");

            int bytesRead;
            int current = 0;
            FileOutputStream fos = null;

            try {

                System.out.println("Connecting...");

                // receive file
                InputStream in = socket.getInputStream();
                byte[] file_size_array = new byte[4];

                int file_size = 0;
                while(file_size < 4) {
                    System.out.println("Reading : " + file_size);
                    System.out.println(file_size_array.toString());
                    file_size += in.read(file_size_array, file_size, 4 - file_size);
                }

                //Reading file size
                System.out.println("Bytes Read : " + file_size);
                file_size = 0;
                file_size |= file_size_array[0] << 8 * 3;
                file_size |= file_size_array[1] << 8 * 2;
                file_size |= file_size_array[2] << 8;
                file_size |= file_size_array[3];
                System.out.println("File Size is : " + file_size);

                System.out.println("Connected!");

                if(file_size  < 0 )
                    continue;

                byte[] byte_array = new byte[file_size];
                // Writing the file to disk
                // Instantiating a new output stream object
                OutputStream output = new FileOutputStream(file_path);

                int count = 0;
                do {
                    bytesRead = in.read(byte_array);

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
    }
}
