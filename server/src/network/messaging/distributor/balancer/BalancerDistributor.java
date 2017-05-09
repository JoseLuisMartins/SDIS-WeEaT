package network.messaging.distributor.balancer;

import com.sun.corba.se.impl.orbutil.ObjectWriter;
import network.load_balancer.LoadBalancer;
import network.messaging.Message;
import network.messaging.distributor.Distributor;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by joao on 5/9/17.
 */
public class BalancerDistributor extends Distributor {

    public final static int REQUEST_SERVER = 0;

    private LoadBalancer loadBalancer;

    public BalancerDistributor(LoadBalancer balancer){
        loadBalancer = balancer;
        addAction(REQUEST_SERVER, (Message m) -> requestServer(m));
    }

    public void requestServer(Message m){

        System.out.println((String)m.getContent());

        try {
            ObjectOutputStream out = new ObjectOutputStream(m.getHttpExchange().getResponseBody());

            out.writeObject(new Message(0, "Hola!",null));

            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
