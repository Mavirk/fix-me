package com.wethinkcode.router;

public class Main {
    public static void main(String args[]) {
        int[] ports = {5000, 5001};
        String ip = "localhost";
        try {
            Router server = new Router();
            server.run();
//            server.stop();
        }catch(Exception e){
            System.out.println("ROUTER ERROR");
            e.printStackTrace();
        }
    }
}
