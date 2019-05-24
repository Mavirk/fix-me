package com.wethinkcode.broker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Client {
    private boolean running = false;
    private AsynchronousSocketChannel client;
    private ByteBuffer buffer;

    public Client(String serverName, int port){
        try {
            client = AsynchronousSocketChannel.open();
            connect(serverName, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void connect(String serverName, int port){
        try {
            Future<Void> result = client.connect(new InetSocketAddress(serverName, port));
            result.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (UnresolvedAddressException e) {
            e.printStackTrace();
        } catch (AlreadyConnectedException e){
            e.printStackTrace();
        }
    }

    public void runClient(){
        running = true;
        while(running){
            sendMessage();
            recieveMessage();
        }
    }
    public void stopclient(){}

    private void sendMessage(){
        String str= "Hello! How are you?";
        buffer = ByteBuffer.wrap(str.getBytes());
        Future<Integer> writeval = client.write(buffer);
        System.out.println("Writing to server: "+str);
        buffer.flip();
        try {
            writeval.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void recieveMessage(){
        Future<Integer> readval = client.read(buffer);
        System.out.println("Received from server: " + new String(buffer.array()).trim());
        try {
            readval.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        buffer.clear();
        running = false;
    }
}
