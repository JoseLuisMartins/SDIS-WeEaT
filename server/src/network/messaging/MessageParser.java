package network.messaging;


import network.messaging.distributor.Distributor;


public class MessageParser {

    private Distributor distributor;

    public MessageParser(Distributor distributor){
        this.distributor = distributor;
    }

    public void ReceiveMessage(Message m ){
        distributor.distribute(m);
    }

    public void Response(Message m){

    }

}
