package com.wethinkcode.market;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Market {
    private boolean running = false;
    private InetSocketAddress serverAddress;
    private SocketChannel clientChannel;
    private int id;
    private ExecutorService messageHandlerPool =  Executors.newFixedThreadPool(10);
    private Model stockRoom;


    public Market(String serverName, int port) throws Exception{
        running = true;
        stockRoom = new Model();
        id = Integer.parseInt(connectServer(serverName, port));
    }

    protected void run() throws IOException{
        String message;
        while(running){
            message = getMessage();
            Runnable worker = new RequestHandler(clientChannel, message, stockRoom);
            messageHandlerPool.execute(worker);
        }
    }

    protected String connectServer(String serverName, int port) throws IOException {
        String response;
        serverAddress = new InetSocketAddress(serverName, port);
        clientChannel = SocketChannel.open(serverAddress);
        // buffer = ByteBuffer.allocate(256);
        response = readBuffer();

        log("Connecting to server: " + serverName + "on port :" + port  + "My ID is : " + response);
        return response;
    }

    protected void disconnectServer(){
        try {
            clientChannel.close();
            // buffer = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMessage() throws IOException{
        String response = null;
        response = readBuffer();
        return response;
    }

    private String readBuffer()throws IOException{
        ByteBuffer buffer = ByteBuffer.allocate(512);
        String response = "";
        String temp = "";
        clientChannel.read(buffer);
        temp = new String(buffer.array()).trim();
        System.out.println("This is before cleaning the Response" + temp);
        String cleanedMessage = temp.substring(1, temp.length()-1);
        response = cleanedMessage;
        buffer.clear();
        return response;
    }

    protected void log(String logMessage){
        System.out.println(logMessage);
    }
}