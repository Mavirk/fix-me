package com.wethinkcode.router;

public class Main {
    public static void main(String args[]) {
        int[] ports = {5000, 5001};
        String ip = "localhost";
        try {
            Server server = new Server(ip, ports);
            server.runServer();
            server.stopServer();
        }catch(Exception e){
            System.out.println("ROUTER ERROR");
            e.printStackTrace();
        }
    }
}
