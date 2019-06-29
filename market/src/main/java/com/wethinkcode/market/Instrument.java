package com.wethinkcode.market;

public class Instrument{
    private String name;
    private int stock;
    private double price;

    Instrument(String name, int stock, double price){
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void removeStock(int stock){
        this.stock -= stock;
    }
    
    public void addStock(int stock){
        this.stock += stock;
    }
}