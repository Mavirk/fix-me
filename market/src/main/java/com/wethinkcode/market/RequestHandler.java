package com.wethinkcode.market;
// import java.util.Map;
import java.nio.channels.SocketChannel;
import java.io.IOException;
import java.nio.ByteBuffer;

public class RequestHandler implements Runnable {
    private final String ACCEPTED = "Executed";
    private final String REJECTED = "Rejected";

    private Model stockRoom;
    private FixMessage fix;
    private Instrument instrument;
    private SocketChannel server;

    RequestHandler(SocketChannel server, String request, Model stockRoom) {
        this.stockRoom = stockRoom;
        fix = new FixMessage(request);
        this.server = server;
    }

    @Override
    public void run() {
        String result;
        log("Attached message " + fix.attachedMessage);
        synchronized (stockRoom) {

            if (fix.attachedMessage.equals("buy")) {
                log("buy message recieved");
                result = buyInstrument();
            }else if (fix.attachedMessage.equals("sell")) {
                result = sellInstrument();
            }else
                result = createReply(REJECTED);
        }
        sendMessage(result);

        System.out.println("Buy request handled");
    }

    private String buyInstrument() {
        instrument = stockRoom.getInstrument(fix.instrument);

        if (instrument != null && instrument.getPrice() <= fix.price && instrument.getStock() >= fix.quantity) {
            instrument.removeStock(fix.quantity);
            Instrument newInstrument = instrument;
            stockRoom.stockMap.replace(fix.instrument, instrument, newInstrument);
            return createReply(ACCEPTED);
        }
        if(instrument == null){
            log("instrument is null");
        }else if(instrument.getPrice() > fix.price ){
            log("trying to buy for less than asking price");
        }else if(instrument.getStock() < fix.quantity){
            log("not enough stock to handle request");
        }
        return createReply(REJECTED);
    }

    private String sellInstrument() {
        instrument = stockRoom.getInstrument(fix.instrument);
        if (instrument != null && instrument.getPrice() >= fix.price) {
            instrument.addStock(fix.quantity);
            Instrument newInstrument = instrument;
            stockRoom.stockMap.replace(fix.instrument, instrument, newInstrument);
            return createReply(ACCEPTED);
        }
        if(instrument == null){
            log("instrument is null");
        }else if(instrument.getPrice() < fix.price ) {
            log("trying to sell for more than market will buy for");
        }
        return createReply(REJECTED);
    }

    private String createReply(String reply) {
        return new FixMessage(fix.receiver, fix.instrument, fix.quantity, fix.price, fix.sender, reply).toString();
    }

    private void sendMessage(String message) {
        message = "{" + message + "}";
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        try {
            server.write(buffer);
        } catch (IOException e) {
            System.out.println("Request Handler Error" + e.getMessage());
        }
        buffer.flip();
    }
    private void log(Object logMessage){
        System.out.println(logMessage);
    }
}