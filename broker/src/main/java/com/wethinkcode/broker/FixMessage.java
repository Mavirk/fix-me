package com.wethinkcode.broker;

import javax.validation.Validation;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class FixMessage {
    @NotNull(message = "Sender Id cannot be null")
    private String sender;
    @Size (min = 2, max = 20, message = "Instrument name must be between 1 and 20 characters")
    @NotNull(message = "Instrument cannot be null")
    private String instrument;
    @NotNull(message = "Quantity cannot be null")
    private int quantity;
    @NotNull(message = "Price cannot be null")
    private double price;
    @NotNull(message = "Market Id cannot be null")
    private String receiver;
    @NotNull(message = "Buy/Sell cannot be null")
    private String attachedMessage;

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