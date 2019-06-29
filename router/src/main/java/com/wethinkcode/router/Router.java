package com.wethinkcode.router;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Router {
    private Map<String, SelectionKey> clientMap = new HashMap<>();
    // private Map <String,SelectionKey> brokerMap = new HashMap<>();
    // private Map <String,SelectionKey> marketMap = new HashMap<>();
    private ExecutorService messageHandlerPool = Executors.newFixedThreadPool(1);

    private Selector selector;
    private ServerSocket marketSocket;
    private ServerSocket brokerSocket;
    private ServerSocketChannel marketChannel;
    private ServerSocketChannel brokerChannel;
    private SelectionKey marketKey;
    private SelectionKey brokerKey;

    // private ByteBuffer writeBuffer = ByteBuffer.allocate(8192);

    private long uniqueId = 0l;
    private boolean running = true;

    public Router(String serverAddress, int marketPort, int brokerPort) throws IOException {
        setupServers(serverAddress, marketPort, brokerPort); // market is port 5000... broker is port 5001
        running = true;
    }

    protected void run() throws IOException {
        log("run method");
        while (running) {
            try {
                System.out.println("Number of selected channels : " + selector.select(3000));
                System.out.println("Number of total channels : " + selector.keys().size());
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey currKey = keyIterator.next();
                    keyIterator.remove();
                    if (currKey.isValid()) {
                        if (currKey.isAcceptable()) {
                            registerClient(currKey);
                        } else if (currKey.isReadable()) {
                            routeMessage(currKey);
                        }
                    } else {
                        log("key not valid");
                        shutdown(-1);
                    }
                }
            } catch (Exception e) {
                
                e.printStackTrace();
                // running = false;
                shutdown(-1);
            }
        }
    }

    private long getUniqueId() {
        return uniqueId = uniqueId + 1;
    }

    private void log(String logMessage) {
        System.out.println(logMessage);
    }

    private void registerClient(SelectionKey currKey) throws IOException {
        String id = String.valueOf(getUniqueId());
        log("client :" + id);
        SocketChannel client;
        ServerSocketChannel server = (ServerSocketChannel) currKey.channel();
        client = server.accept();
        client.configureBlocking(false);
        SelectionKey key = client.register(selector, SelectionKey.OP_READ, id);

        clientMap.put(id, key);
        writeBuffer(client, id);
        log("client registered");
    }

    private void setupServers(String serverAddress, int marketPort, int brokerPort) throws IOException {
        selector = Selector.open();
        marketChannel = ServerSocketChannel.open();
        brokerChannel = ServerSocketChannel.open();
        marketChannel = marketChannel.bind(new InetSocketAddress(serverAddress, marketPort));
        brokerChannel = brokerChannel.bind(new InetSocketAddress(serverAddress, brokerPort));
        marketChannel.configureBlocking(false);
        brokerChannel.configureBlocking(false);
        marketKey = marketChannel.register(selector, SelectionKey.OP_ACCEPT, "5000");
        brokerKey = brokerChannel.register(selector, SelectionKey.OP_ACCEPT, "5001");
        log("severs setup");
    }

    private void routeMessage(SelectionKey key) throws IOException {
        log("routeMessage()");
        Runnable worker = new MessageHandler(clientMap, key);
        messageHandlerPool.execute(worker);
    }

    protected void writeBuffer(SocketChannel client, String message) throws IOException {
        message = "{" + message + "}";
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        client.write(buffer);
        buffer.clear();
    }

    private void shutdown(int exitCode) {
        Iterator iterator = clientMap.entrySet().iterator();
        messageHandlerPool.shutdown();
        try {
            messageHandlerPool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        while(iterator.hasNext()){
            Map.Entry<String, SelectionKey> client  = (Map.Entry)iterator.next();
            SelectionKey key  = client.getValue();
            SocketChannel channel = (SocketChannel) key.channel();
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            brokerChannel.close();
            marketChannel.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        System.exit(exitCode);
    }

    private void sendError(SelectionKey key, int errorCode){
        try {
            switch(errorCode){
                case 100:
                    writeBuffer((SocketChannel) key.channel(), "Invalid Market Request -- market does not exist.");
                    break;
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
    // private void routeMessage(SelectionKey key) throws IOException{
    //     Runnable worker = new MessageHandler(clientMap, key);
    //     messageHandlerPool.execute(worker);

    //     // String brokerMessage = readBuffer((SocketChannel)key.channel());
    //     // if (brokerMessage.charAt(0) == '{' && brokerMessage.endsWith("}")){
    //     //     log("this is a complete message");
    //     //     String cleanedMessage = brokerMessage.substring(1, brokerMessage.length()-1);
    //     //     String[] splitMessage = cleanedMessage.split(",");
    //     //     if(clientMap.containsKey(splitMessage[4])){
    //     //         SelectionKey destination = clientMap.get(splitMessage[4]);
    //     //     }else {
    //     //         log("Invalid destination");
    //     //         sendError(key, 100);
    //     //     }
    //     // }else{
    //     //     log("inccomplete message");
    //     //     System.exit(0);
    //     // }
    //     // log(brokerMessage);
    // }

        // synchronized (this.pendingData) {
        //     List queue = (List) this.pendingData.get(socketChannel);

        //     // Write until there's not more data ...
        //     while (!queue.isEmpty()) {
        //         ByteBuffer buf = (ByteBuffer) queue.get(0);
        //         socketChannel.write(buf);
        //         if (buf.remaining() > 0) {
        //             // ... or the socket's buffer fills up
        //             break;
        //         }
        //         queue.remove(0);
        //     }

        //     if (queue.isEmpty()) {
        //         // We wrote away all data, so we're no longer interested
        //         // in writing on this socket. Switch back to waiting for
        //         // data.
        //         key.interestOps(SelectionKey.OP_READ);
        //     }
        // }
        // Process any pending changes
        // synchronized(this.changeRequests) {
        //     Iterator changes = this.changeRequests.iterator();
        //     while (changes.hasNext()) {
        //         ChangeRequest change = (ChangeRequest) changes.next();
        //         switch(change.type) {
        //             case ChangeRequest.CHANGEOPS:
        //                 SelectionKey key = change.socket.keyFor(this.selector);
        //                 key.interestOps(change.ops);
        //         }
        //     }
        //     this.changeRequests.clear();
        // }

    // protected String readBuffer(SocketChannel client)throws IOException{
    //     ByteBuffer buffer = ByteBuffer.allocate(512);
    //     String response = "";
    //     String temp = "";
    //     client.read(buffer);
    //     temp = new String(buffer.array()).trim();
    //     response = temp;
    //     buffer.clear();
    //     return response;
    // }

    // public void send(SocketChannel socket, byte[] data) {
    //     synchronized (this.changeRequests) {
    //         // Indicate we want the interest ops set changed
    //         this.changeRequests.add(new ChangeRequest(socket, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

    //         // And queue the data we want written
    //         synchronized (this.pendingData) {
    //             List queue = (List) this.pendingData.get(socket);
    //             if (queue == null) {
    //                 queue = new ArrayList();
    //                 this.pendingData.put(socket, queue);
    //             }
    //             queue.add(ByteBuffer.wrap(data));
    //         }
    //     }

    //     // Finally, wake up our selecting thread so it can make the required changes
    //     this.selector.wakeup();
    // }