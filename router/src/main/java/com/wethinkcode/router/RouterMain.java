package com.wethinkcode.router;

public class RouterMain {
    public static void main(String args[]) {
        int[] ports = {5000, 5001};
        String ip = "localhost";
        try {
            Router server = new Router(ip, ports[1], ports[0]);
            server.run();
//            server.stop();
        }catch(Exception e){
            System.out.println("ROUTER ERROR");
            e.printStackTrace();
        }
    }
}
