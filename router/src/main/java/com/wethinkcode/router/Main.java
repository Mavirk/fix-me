package com.wethinkcode.router;

import java.net.ServerSocket;

public class Main {
    private static long uniqueId = 0l;
    public static void main(String args[]) {
        try{
            Runnable brokerHandler = new BrokerPortHandler(new ServerSocket(5000));
            Runnable marketHandler = new MarketPortHandler(new ServerSocket(5001));
            new Thread(brokerHandler).start();
            new Thread(marketHandler).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static long getUniqueId() {
        return uniqueId = uniqueId + 1;
    }
}
