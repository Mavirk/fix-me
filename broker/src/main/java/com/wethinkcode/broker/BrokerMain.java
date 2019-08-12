package com.wethinkcode.broker;

import java.io.IOException;

public class BrokerMain {
    public static void main(String[] args) {
        try {
            Broker broker = new Broker("localhost", 5000);
            broker.run();
        }catch (IOException ioe){
            System.out.println();
            System.out.println("Server disconnected unexpectedly with message: " + ioe.getMessage());
        }catch(Exception e){
            System.out.println("BROKER ERROR :");
        }
    }
}
