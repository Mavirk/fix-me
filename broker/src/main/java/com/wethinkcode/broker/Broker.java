package com.wethinkcode.broker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Broker {
    private boolean running = false;
    private InetSocketAddress serverAddress;
    private ServerSocketChannel server;
    private SocketChannel socketChannel;
    private ByteBuffer readBuffer = ByteBuffer.allocate(8192);
    private ByteBuffer writeBuffer = ByteBuffer.allocate(8192);

    public Broker(String serverName, int port) throws Exception{
        running = true;
        connectServer(serverName, port);
    }

    protected void run(){
        while (running){

        }
    }

    protected void connectServer(String serverName, int port) throws IOException {
        serverAddress = new InetSocketAddress(serverName, port);
        socketChannel = SocketChannel.open(serverAddress);
        log("Connecting to server: " + serverName +"on port :" + port);
    }

    protected void disconnectServer(){}

    protected String read()throws IOException{
        String message = "test";
        int readNum = 0;
        readNum = socketChannel.read(readBuffer);

        if (readNum == -1){
            log("ERROR get message failed");
            shutdown();
        }

        return message;
    }

    protected void write(String message){

    }

    protected void log(String logMessage){
        System.out.println(logMessage);
    }

    protected String getUserInput(String question){
        Scanner consoleInput = new Scanner(System.in);
        log(question);
        return consoleInput.nextLine();
    }

    private void shutdown() {
        System.exit(0);
    }
//  protected void buy (){}

//   protected void sell(){}

}
