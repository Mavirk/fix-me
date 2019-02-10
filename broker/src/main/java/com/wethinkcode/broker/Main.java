package com.wethinkcode.broker;

public class Main {
    public static void main(String args[]) {
        try {
            Broker broker = new Broker("localhost", 5001);
            broker.run();
        }catch(Exception e){
            System.out.println("BROKER ERROR :");
            e.printStackTrace();
        }
    }
}
