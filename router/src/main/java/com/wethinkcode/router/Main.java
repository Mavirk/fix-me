package com.wethinkcode.router;

public class Main {
    public static void main(String args[]) {
        int[] ports = {5000, 5001};
        try {
            Router router = new Router();
            router.run();
        }catch(Exception e){
            System.out.println("ROUTER ERROR");
            e.printStackTrace();
        }
    }
}
