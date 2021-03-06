package com.wethinkcode.router;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;


public class MessageHandler implements Runnable{
    
    private Map <String ,SelectionKey> clientMap;
    private SelectionKey key;
    private PrintWriter writer = null;

    MessageHandler(Map <String ,SelectionKey> clientMap, SelectionKey key){
        this.clientMap = clientMap;
        this.key = key;
        try {
            writer = new PrintWriter("handlerlogs/handlerLog.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        log("New handler created.");
    }

    @Override
    public void run() {
        String brokerMessage;
        try {
            brokerMessage = readBuffer((SocketChannel) key.channel());
            System.out.println("This is from the message handler" + brokerMessage);
            log("Client "  + key.attachment()  + " Message : "+ brokerMessage);
            if (brokerMessage.charAt(0) == '{' && brokerMessage.endsWith("}")){
                log("this is a complete message");
                String cleanedMessage = brokerMessage.substring(1, brokerMessage.length()-1);
                String[] splitMessage = cleanedMessage.split(",");
                if(clientMap.containsKey(splitMessage[4])){
                    SelectionKey destination = clientMap.get(splitMessage[4]);
                    writeBuffer((SocketChannel) destination.channel(), cleanedMessage);
                }
                else {
                    log("Invalid destination");
                    sendError(key, 100);
                }
            }else{
                log("inccomplete message");
            }
            log(brokerMessage);     
        } catch (IOException e) {
            // TODO Auto-generated catch block
            log(e.getMessage());
            Router.removeFromProcessing(key);
            Runtime.getRuntime().halt(-1);
        }
        Router.removeFromProcessing(key);
//        writer.close();
    }

    private String readBuffer(SocketChannel client)throws IOException{
        ByteBuffer buffer = ByteBuffer.allocate(512);
        String response = "";
        String temp = "";
        client.read(buffer);
        temp = new String(buffer.array()).trim();
        response = temp;
        return response;
    }

    private void writeBuffer(SocketChannel client, String message) throws IOException {
        message = "{" + message + "}";        
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        client.write(buffer);
        // buffer.clear();
    }

    private void log(Object logMessage){
        writer.print(logMessage);
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