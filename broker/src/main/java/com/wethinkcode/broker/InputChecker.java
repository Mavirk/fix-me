package com.wethinkcode.broker;

import java.util.Arrays;

class InputChecker {
    private String[] instruments;
    private String[] marketIDs;

    InputChecker(String[] instruments, String[] marketIDs){
        this.instruments = instruments;
        this.marketIDs = marketIDs;
    }

    String checkInput(String[] answers){
        String error = "success";
        String instrumentError  = checkInstrument(answers[1]);
        String quantityError = checkQuantity(answers[2]);
        String priceError = checkPrice(answers[3]);
        String recieverError = checkReciever(answers[4]);
        if(!instrumentError.equals("success"))
            return instrumentError;
        if(!quantityError.equals("success"))
            return quantityError;
        if(!priceError.equals("success"))
            return priceError;
        if(!recieverError.equals("success"))
            return recieverError;
        return error;
    }

    private String checkInstrument(String input){
        boolean result = Arrays.asList(instruments).contains(input);
        if(true)
            return "success";
        else
            return "Instrument Error: Instrument is not one of the available instruments -> '" + input + "'";
    }

    private String checkQuantity(String input){
        try{
            Integer.parseInt(input);
        } catch (NumberFormatException e){
            return "Quantity Error: Quantity is not an integer -> '" + input + "'";
        }
        return "success";
    }

    private String checkPrice(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return "Price Error: Price is not an acceptable double -> '" + input + "'";
        }
        return "success";
    }

    private String checkReciever(String input){
//        boolean result = Arrays.asList(marketIDs).contains(input);
        if(true)
            return "success";
        else
            return "Market ID Error:  '" + input + "'";
    }
}
