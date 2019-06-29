package com.wethinkcode.market;
import java.util.Map;
import java.util.HashMap;


public class Model{
    public Map <String ,Instrument> stockMap = new HashMap<>();
    private String[] stocklist = {  
        "apple,100,5",
        "banna,150,7",
        "pear,50,10",
        "grape,400,2"
    };

    Model(){
        populateStock();
    }

    private void populateStock(){
        Instrument temp;
        for(String stock : stocklist){
            String[] stockItem = stock.split(",");
            String name = stockItem[0];
            int quantity = Integer.parseInt(stockItem[1]);
            double price = Double.parseDouble(stockItem[2]);
            temp = new Instrument(name, quantity, price);
            addStock(temp);
        }
    }

    public void addStock(Instrument item){
        System.out.println("Adding stock item: ...");
        System.out.println(item.getName());
        System.out.println(item.getStock());
        System.out.println(item.getPrice());
        stockMap.put(item.getName(), item);
    }

    public Instrument getInstrument(String stockName){
        return stockMap.get(stockName);
    }
}