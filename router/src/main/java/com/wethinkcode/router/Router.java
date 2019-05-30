package com.wethinkcode.router;

import com.sun.javafx.collections.MappingChange;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Router {
    private Map <String ,SelectionKey> clientMap = new HashMap<>();
    private Map <String,SelectionKey> brokerMap = new HashMap<>();
    private Map <String,SocketChannel> marketMap = new HashMap<>();
    private ExecutorService readThreadPool =  Executors.newFixedThreadPool(10);
    private ExecutorService writeThreadPool =  Executors.newFixedThreadPool(10);
    private EchoWorker echoWorker;

    // A list of ChangeRequest instances
    private List changeRequests = new LinkedList();

    // Maps a SocketChannel to a list of ByteBuffer instances
    private Map pendingData = new HashMap();

    private Selector selector;
    private ServerSocket marketSocket;
    private ServerSocket brokerSocket;
    private ServerSocketChannel marketChannel;
    private ServerSocketChannel brokerChannel;
    private SelectionKey marketKey;
    private SelectionKey brokerKey;

    private ByteBuffer writeBuffer = ByteBuffer.allocate(8192);

    private long uniqueId = 0l;
    private boolean running = true;

    public Router() throws IOException {
        new Thread(echoWorker).start();
        setupServers();
        running = true;
    }


    protected void run() throws IOException{
        log("run method");
        while (running){
            try {
                // Process any pending changes
                synchronized(this.changeRequests) {
                    Iterator changes = this.changeRequests.iterator();
                    while (changes.hasNext()) {
                        ChangeRequest change = (ChangeRequest) changes.next();
                        switch(change.type) {
                            case ChangeRequest.CHANGEOPS:
                                SelectionKey key = change.socket.keyFor(this.selector);
                                key.interestOps(change.ops);
                        }
                    }
                    this.changeRequests.clear();
                }
                System.out.println("Number of selected channels : " +  selector.select());
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while(keyIterator.hasNext()) {
                    SelectionKey currKey = keyIterator.next();
                    if (currKey.channel() instanceof ServerSocketChannel){
                        log("Key is of instance ServerSocket");
                    }
                    if (currKey.channel() instanceof SocketChannel){
                        log("Key is of instance Socket");
                    }
                    if (currKey.isValid()) {
                        if (currKey.isAcceptable()) {
                            if (currKey.equals(brokerKey))
                                registerBroker(currKey);
                            if (currKey.equals(marketKey))
                                registerMarket(currKey);
                        } else if (currKey.isReadable()){
//                            if (currKey.equals(brokerKey))
//                                readBroker(currKey);
//                            if (currKey.equals(marketKey))
//                                readMarket();
                            read(currKey);
                        } else if (currKey.isWritable()) {
//                            if (currKey.equals(brokerKey))
//                                writeBroker(currKey);
//                            if (currKey.equals(marketKey))
//                                writeMarket();
                            write(currKey);
                        }
                    }
                    else {
                        log("key not valid");
                        shutdown(-1);
                    }
                    keyIterator.remove();
                }
            }catch (Exception e){
                e.printStackTrace();
                shutdown(-1);
            }
        }

    }

    private long getUniqueId() {
        return uniqueId = uniqueId + 1;
    }

    private void log(String logMessage){
        System.out.println(logMessage);
    }


    private void registerMarket(SelectionKey currKey) throws IOException{
        SocketChannel client;
        ServerSocketChannel server = (ServerSocketChannel) currKey.channel();
        client = server.accept();
        log("market registered");
    }

    private void registerBroker(SelectionKey currKey) throws IOException{
        String id = String.valueOf(getUniqueId());
        log("broker Id :" + id);
        SocketChannel client;
        ServerSocketChannel server = (ServerSocketChannel) currKey.channel();
        client = server.accept();
        client.configureBlocking(false);
        SelectionKey key = client.register(selector, SelectionKey.OP_READ);

        brokerMap.put(id, key);
        clientMap.put(id, key);
        writeBuffer(client, id);
        log("broker registered");
    }

    private void setupServers() throws IOException{
        selector = Selector.open();
        marketChannel = ServerSocketChannel.open();
        brokerChannel = ServerSocketChannel.open();
        marketSocket = marketChannel.socket();
        brokerSocket = brokerChannel.socket();
        marketSocket.bind(new InetSocketAddress("localhost",5001));
        brokerSocket.bind(new InetSocketAddress("localhost",5000));
        marketChannel.configureBlocking(false);
        brokerChannel.configureBlocking(false);
        marketKey = marketChannel.register(selector, SelectionKey.OP_ACCEPT);
        brokerKey = brokerChannel.register(selector, SelectionKey.OP_ACCEPT);
//        brokerSocket.setReuseAddress(true);
        log("severs setup");
    }

    private void read(SelectionKey key) throws IOException{
    }

    private void write(SelectionKey key)throws IOException{
        log("write() function called");
        SocketChannel socketChannel = (SocketChannel) key.channel();

        synchronized (this.pendingData) {
            List queue = (List) this.pendingData.get(socketChannel);

            // Write until there's not more data ...
            while (!queue.isEmpty()) {
                ByteBuffer buf = (ByteBuffer) queue.get(0);
                socketChannel.write(buf);
                if (buf.remaining() > 0) {
                    // ... or the socket's buffer fills up
                    break;
                }
                queue.remove(0);
            }

            if (queue.isEmpty()) {
                // We wrote away all data, so we're no longer interested
                // in writing on this socket. Switch back to waiting for
                // data.
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    public void send(SocketChannel socket, byte[] data) {
        synchronized (this.changeRequests) {
            // Indicate we want the interest ops set changed
            this.changeRequests.add(new ChangeRequest(socket, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

            // And queue the data we want written
            synchronized (this.pendingData) {
                List queue = (List) this.pendingData.get(socket);
                if (queue == null) {
                    queue = new ArrayList();
                    this.pendingData.put(socket, queue);
                }
                queue.add(ByteBuffer.wrap(data));
            }
        }

        // Finally, wake up our selecting thread so it can make the required changes
        this.selector.wakeup();
    }
    private void shutdown(int exitCode){
        Iterator iterator = clientMap.entrySet().iterator();
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

    protected String readBuffer(SocketChannel client)throws IOException{
        ByteBuffer buffer = ByteBuffer.allocate(256);
        String response;
        client.read(buffer);
        buffer.clear();
        response = new String(buffer.array()).trim();
        return response;
    }

    protected void writeBuffer(SocketChannel client, String message) throws IOException {

        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        client.write(buffer);
        buffer.clear();
    }
}
