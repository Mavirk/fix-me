package com.wethinkcode.market;


public class FixMessage{
    String sender;
    String instrument;
    int quantity;
    double price;
    String receiver;
    String attachedMessage;

    FixMessage(String message){
        String[] messageArray = message.split(",");

        sender = messageArray[0];
        instrument = messageArray[1];
        quantity = Integer.parseInt(messageArray[2]);
        price = Double.parseDouble(messageArray[3]);
        receiver = messageArray[4];
        attachedMessage = messageArray[5];
    }

    FixMessage(String sender, String instrument, int quantity, double price, String receiver, String attachedMessage){
        this.sender = sender;
        this.instrument = instrument;
        this.quantity = quantity;
        this.price =  price;
        this.receiver = receiver;
        this.attachedMessage = attachedMessage;
    }

    public String toString(){
        String[] array = {
            sender,
            instrument,
            Integer.toString(quantity),
            Double.toString(price),
            receiver,
            attachedMessage
        };

        String fixString = String.join(",", array);
        return fixString;
    }
}