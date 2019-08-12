package com.wethinkcode.broker;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Broker {
    private boolean running = false;
    private InetSocketAddress serverAddress;
    private SocketChannel clientChannel;
    private int id;
    private Scanner consoleInput = new Scanner(System.in);
    private Map<String, String []> brokerMap = new HashMap<>();

    private String[] instruments = {
            "apple",
            "banana",
            "grape"
    };

    private String[] marketIDs = {
            "2"
    };

    public Broker(String serverName, int port) throws IOException{
        running = true;
        id = Integer.parseInt(connectServer(serverName, port));
    }

    protected void run() throws IOException{
        String[] response;
        String message;
        while (running){
            printBuyOptions();
            message = getUserInput();
//            message = testInput(); // THIS LINE IS FOR TESTING
            if(!message.equals("Error")) {
                response = sendMessage(message).split(",");
//                log(" Test Response: " + String.join(",", response));
                if(response[response.length - 1].equals("Rejected")) {
                    log("Response: " + response[response.length - 1]);
                    log(response[response.length - 2]);
                }else if(response[response.length - 1].equals("Executed")){
                    log("Response: " + response[response.length - 1]);
                }
            }
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

    private String sendMessage(String message) throws IOException{
        log("----------------------");
//        log("Request : " + message);
        String response = null;
//        try {
            message = "{" + message + "}";
            writeBuffer(message);
            response = readBuffer();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return response;
    }

    private String readBuffer()throws IOException{
        ByteBuffer buffer = ByteBuffer.allocate(512);
        String response = "";
        String temp = "";
        clientChannel.read(buffer);
        temp = new String(buffer.array()).trim();
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

    private String testInput(){
        log("Press ENTER to send test message");
        consoleInput.nextLine();
        return "2,apple,2,2,1,Buy";
    }
    private String getUserInput(){
        String[] questions =  {
            "",
            "Instrument?",
            "Quantity?",
            "Price?",
            "Market?",
            "Buy/Sell?"
        };
        String[] answers = new String[6];
        answers[0] = Integer.toString(id);
        for(int i = 1; i < 6; i++) {
            log(questions[i]);
            answers[i] = consoleInput.nextLine();
        }
        InputChecker inputChecker = new InputChecker(instruments, marketIDs);

        String checkerResponse = inputChecker.checkInput(answers);
        if(checkerResponse.equals("success")){
            FixMessage fixMessage = new FixMessage(
                    answers[0],
                    answers[1],
                    Integer.parseInt(answers[2]),
                    Double.parseDouble(answers[3]),
                    answers[4],
                    answers[5]
            );
            return fixMessage.toString();
        }else{
            log(checkerResponse);
        }
        return "Error";
    }

    private void shutdown() {
        System.exit(0);
    }

    protected void log(Object logMessage){
        System.out.println(logMessage);
    }

    private void printBuyOptions(){
        log("----------------------");
        log("");
        log("Instruments for sale :");
        log("apple");
        log("banana");
        log("grape");
    }
}
