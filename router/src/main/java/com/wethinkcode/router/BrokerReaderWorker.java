package com.wethinkcode.router;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class BrokerReaderWorker implements Callable<Boolean> {
    private SocketChannel market;
    private SelectionKey brokerKey;
    private HashMap<String, SelectionKey> clients;
    private ByteBuffer readBuffer = ByteBuffer.allocate(512);
    private ByteBuffer writeBuffer;
    public BrokerReaderWorker(SelectionKey brokerKey, HashMap<String , SelectionKey> clients){
        this.brokerKey = brokerKey;
        this.clients = clients;
    }

    @Override
    public Boolean call() throws Exception {
        System.out.println("Inside BrokerReaderWorker");
        SocketChannel client = (SocketChannel) brokerKey.channel();
        String response;

        client.read(readBuffer);
        response = new String(readBuffer.array()).trim();

        writeBuffer = ByteBuffer.wrap(response.getBytes());
        readBuffer.clear();

        String[] message = response.split(",");

        if(clients.containsKey(message[0])) {
            market = (SocketChannel) clients.get(message[0]).channel();
            market.write(writeBuffer);
            return true;
        }
        return false;
    }
}
