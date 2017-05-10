package gui;


import network.Server;

public class WeEatServer {

    public static void main(String [ ] args) throws Exception {

        System.out.println("WeEat Server");

        Server server= new Server("127.0.0.1",8000,8888);
    }
}
