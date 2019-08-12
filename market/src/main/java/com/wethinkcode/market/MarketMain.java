package com.wethinkcode.market;

import java.io.IOException;

public class MarketMain {
    public static void main(String[] args) {
        try {
            Market market = new Market("localhost", 5001);
            market.run();
        }catch (IOException ioe){
            System.out.println();
            System.out.println("Server disconnected unexpectedly with message: " + ioe.getMessage());
        }catch(Exception e){
            System.out.println("MARKET ERROR :");
        }
    }
}
