package network.sockets;

import java.io.*;

public class SecureClientQuarters extends SecureClient{

    private static String confirmationCode = "Batata";
    private String location;

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
        this.location = location;

        handShake();
        this.start();

    }

    public void handShake() throws IOException {

        writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println(confirmationCode + location);
        inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String res = inputStream.readLine();

        System.out.println("MODE" + res);
    }

    @Override
    public void run() {

        while (true){
            try{

                String msg = inputStream.readLine();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
