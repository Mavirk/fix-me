package com.wethinkcode.router;

import java.net.ServerSocket;
import java.net.Socket;

public class BrokerPortHandler implements Runnable{
    ServerSocket serverSocket;
    Socket socket;
    public boolean running = true;

    public BrokerPortHandler(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
    public void run(){
        while(running){
            try (Socket clientSocket = serverSocket.accept()) {
                socket = clientSocket;
                checkSocket(socket);
                System.out.println("Broker Connected...");
                Runnable routerWorker = new RouterWorkerThread(socket);
                new Thread(routerWorker).start();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
    public void checkSocket(Socket socket){
        if (socket.isConnected()){
            System.out.println("Broker is Connected...");
        }
        if (socket.isClosed()){
            System.out.println("Broker is Closed");
        }
    }
    public void processMessage(String message){
        FixMessage fixMessage = new FixMessage(message);
    }
}

