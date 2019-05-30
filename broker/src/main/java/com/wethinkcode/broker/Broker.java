package com.wethinkcode.broker;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

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
    private ServerSocketChannel serverChannel;
    private SocketChannel clientChannel;
    private ByteBuffer buffer;
    private String id;


    public Broker(String serverName, int port) throws Exception{
        running = true;
        id = connectServer(serverName, port);
    }

    protected void run(){
        String message ;
        while (running){
            message = getUserInput("Please Input Message");
            sendMessage(message);
            System.out.println("Message Sent");
            break;
        }
        disconnectServer();
    }

    protected String connectServer(String serverName, int port) throws IOException {
        String response;
        serverAddress = new InetSocketAddress(serverName, port);
        clientChannel = SocketChannel.open(serverAddress);
        buffer = ByteBuffer.allocate(256);
        response = readBuffer();
        log("Connecting to server: " + serverName +"on port :" + port  + "My ID is : " + response);
        return response;
    }

    protected void disconnectServer(){
        try {
            clientChannel.close();
            buffer = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String sendMessage(String message){
        String response = null;
        try {
            writeBuffer(message);
            response = readBuffer();
            System.out.println("Reponse : " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private String readBuffer()throws IOException{
        String response;
        clientChannel.read(buffer);
        response = new String(buffer.array()).trim();
        buffer.clear();
        return response;
    }

    private void writeBuffer(String message) throws IOException {
        buffer = ByteBuffer.wrap(message.getBytes());
        clientChannel.write(buffer);
        buffer.clear();
    }


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
