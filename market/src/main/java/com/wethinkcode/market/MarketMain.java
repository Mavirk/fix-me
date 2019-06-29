package com.wethinkcode.market;

public class MarketMain {
    public static void main(String args[]) {
        try {
            Market market = new Market("localhost", 5001);
            market.run();
        }catch(Exception e){
            System.out.println("MARKET ERROR :");
            e.printStackTrace();
        }
    }
}
