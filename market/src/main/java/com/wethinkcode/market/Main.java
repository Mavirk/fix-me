package com.wethinkcode.broker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String args[]) {
        try (
                Scanner scan = new Scanner(System.in);
                Socket brokerSocket = new Socket("localhost", 5000);
                PrintWriter routerOut = new PrintWriter(brokerSocket.getOutputStream(),true);
                BufferedReader routerIn = new BufferedReader(new InputStreamReader(brokerSocket.getInputStream()));)
        {
            String fromRouter, fromUser;
            while ((fromRouter = routerIn.readLine()) != null) {
                System.out.println("Server: " + fromRouter);
                if (fromRouter.equals("bye")) System.exit(0);
                fromUser = scan.next();
                if (fromUser != null){
                    System.out.println("Client: " + fromUser);
                    routerOut.println(fromUser);
                }
            }
        } catch(Exception e){ e.printStackTrace();}
    }
}
