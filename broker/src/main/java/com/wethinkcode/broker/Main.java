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
                Socket socket = new Socket("localhost", 5000);
                PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));)
        {
            String fromRouter;
            String fromUser;
            while ((fromUser = scan.next()) != "shutdown") {
                out.println(fromUser);
                fromRouter = in.readLine();
                System.out.println("Router says" + fromRouter);
            }
            if(fromUser.equals("shutdown"))System.out.println("Broker shutting down");
        } catch(Exception e){ e.printStackTrace();}
    }
}
