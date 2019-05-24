package com.wethinkcode.broker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Broker {
    private boolean running = false;
    private InetSocketAddress serverAddress;
    private ServerSocketChannel server;
    private SocketChannel socketChannel;


    public Broker(String serverName, int port) throws Exception{
        running = true;
        connectServer(serverName, port);
    }

    protected void run(){
        while (running){

        }
        disconnectServer();
    }

    protected void connectServer(String serverName, int port) throws IOException {
        serverAddress = new InetSocketAddress(serverName, port);
        socketChannel = SocketChannel.open(serverAddress);
        log("Connecting to server: " + serverName +"on port :" + port);
    }

    protected void disconnectServer(){
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    protected String read()throws IOException{
//
//    }
//
//    protected void write(String message){
//
//    }


    protected String getUserInput(String question){
        Scanner consoleInput = new Scanner(System.in);
        log(question);
        return consoleInput.nextLine();
    }

    private void shutdown() {
        System.exit(0);
    }

    protected void log(String logMessage){
        System.out.println(logMessage);
    }
}
