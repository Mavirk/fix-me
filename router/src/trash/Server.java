package com.wethinkcode.router;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Server {

    private boolean running = false;
    private AsynchronousServerSocketChannel server;
    private List<AsynchronousSocketChannel> clients = new ArrayList<AsynchronousSocketChannel>();

    public Server(String ip, int[] ports){
        try{
            server = AsynchronousServerSocketChannel.open();
            for (int i : ports)
                server.bind(new InetSocketAddress(ip, ports[i]));
        }catch (Exception e){
            print(e.getMessage());
        }
    }

    public void runServer(){
        running = true;
        while(running) {
            if(acceptClientConnection())
                print("New client added ...");
            else running = false;
        }
    }

    public void stopServer(){
        Iterator<AsynchronousSocketChannel> i = clients.iterator();
        while(i.hasNext()){
            try {
                i.next().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private boolean acceptClientConnection(){
        Future<AsynchronousSocketChannel> acceptCon = server.accept();
        try {
            clients.add(acceptCon.get(10, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        } catch (TimeoutException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private void print(String message){
        System.out.println(message);
    }
}
