package com.wethinkcode.broker;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Broker {
    private boolean running = false;
    private InetSocketAddress serverAddress;
    private SocketChannel clientChannel;
    private int id;


    public Broker(String serverName, int port) throws Exception{
        running = true;
        id = Integer.parseInt(connectServer(serverName, port));
    }

    protected void run(){
        String message ;
        while (running){
            printBuyOptions();
            message = getUserInput();
            sendMessage(message);
            System.out.println("Message Sent");
        }
        disconnectServer();
    }

    protected String connectServer(String serverName, int port) throws IOException {
        String response;
        serverAddress = new InetSocketAddress(serverName, port);
        clientChannel = SocketChannel.open(serverAddress);
        // buffer = ByteBuffer.allocate(256);
        response = readBuffer();

        log("Connecting to server: " + serverName +"on port :" + port  + "My ID is : " + response);
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

    private String sendMessage(String message){
        String response = null;
        try {
            message = "{" + message + "}";
            writeBuffer(message);
            response = readBuffer();
            System.out.println("Reponse : " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void writeBuffer(String message) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        clientChannel.write(buffer);
        buffer.flip();
    }
    
    protected String getUserInput(){
        String[] questions =  {
            "Instrument?",
            "Quantity?",
            "Price?",
            "Market?",
            "Buy/Sell?"
        };
        Scanner consoleInput = new Scanner(System.in);
        String finalString = "";

        finalString = finalString + id;
        for(int i = 0; i < 5; i++){
            finalString = finalString + ",";
            log(questions[i]);
            consoleInput.hasNextLine();
            String part = consoleInput.nextLine();
            finalString = finalString + part;
        }
        consoleInput.close();
        log("FINAL STRING : " + finalString);
        return finalString;
    }

    private void shutdown() {
        System.exit(0);
    }

    protected void log(String logMessage){
        System.out.println(logMessage);
    }

    private void printBuyOptions(){
        log("apple");
        log("banana");
        log("grape");
    }
}
