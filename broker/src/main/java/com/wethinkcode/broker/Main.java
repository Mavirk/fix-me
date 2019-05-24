package com.wethinkcode.broker;

public class Main {
    public static void main(String args[]) {
        try {
            Client client = new Client("localhost", 5001);
            client.runClient();
        }catch(Exception e){
            System.out.println("BROKER ERROR :");
            e.printStackTrace();
        }
    }
}
