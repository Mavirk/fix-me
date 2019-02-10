package com.wethinkcode.router;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Router {
    private ArrayList<SocketChannel> clients = new ArrayList<>();
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
    private ByteBuffer readBuffer = ByteBuffer.allocate(8192);
    private ByteBuffer writeBuffer = ByteBuffer.allocate(8192);

    private long uniqueId = 0l;
    private boolean running = true;

    public void Router() throws IOException {
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

                selector.select();
                Set<SelectionKey> clientKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = clientKeys.iterator();

                while(keyIterator.hasNext()) {
                    SelectionKey currKey = keyIterator.next();

                    if (currKey.isValid()) {
                        if (currKey.isAcceptable()) registerClient(currKey);
                        else if (currKey.isReadable()) read(currKey);
                        else if (currKey.isWritable()) write(currKey);
                    }
                    else {
                        log("key not valid");
                        shutdown(-1);
                    }
                }
                shutdown(1);

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

    private void registerChannel (Selector selector, SelectableChannel channel, int ops) throws IOException {
        if (channel == null) return;

        // Set the new channel nonblocking
        channel.configureBlocking (false);

        // Register it with the selector
        channel.register (selector, ops);
    }

    private void registerClient(SelectionKey currKey) throws IOException{
        SocketChannel client;
        ServerSocketChannel server = (ServerSocketChannel) currKey.channel();
        client = server.accept();
        clients.add(client);
        registerChannel(selector, client, SelectionKey.OP_READ);
        log("client registered");
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
        log("severs setup");
    }

    private void read(SelectionKey key) throws IOException{
        int readNum = 0;
        SocketChannel client = (SocketChannel) key.channel();
        readBuffer.clear();
        try{
            readNum = client.read(readBuffer);
        }catch (IOException e){
            client.close();
            key.cancel();
            return;
        }
        if (readNum == -1){
            client.close();
            key.cancel();
            return;
        }
        this.echoWorker.processData(this, client, this.readBuffer.array(), readNum);
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
        System.exit(exitCode);
    }
}
