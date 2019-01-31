package com.wethinkcode.router;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MarketPortHandler implements Runnable{
    ServerSocket serverSocket;
    Socket socket;
    public boolean running = true;

    public MarketPortHandler(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
    public void run(){
        while(running){
            try (
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ){
                socket = clientSocket;

                System.out.println("Market Connected...");
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
    public void processMessage(String message){
        FixMessage fixMessage = new FixMessage(message);
    }
}

