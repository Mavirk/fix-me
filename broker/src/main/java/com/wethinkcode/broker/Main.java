package com.wethinkcode.broker;

public class Main {
    public static void main(String args[]) {
        try {
            Broker broker = new Broker("localhost", 5000);
            broker.run();
        }catch(Exception e){
            System.out.println("BROKER ERROR :");
            e.printStackTrace();
        }
    }
}
