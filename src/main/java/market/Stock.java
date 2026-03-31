package market;

import bots.TradingBotBasic;

public class Stock{
    private int shares;
    private String name;
    private float price;
    private TradingBotBasic owner;
    private int dayMade;

    public Stock(int shares, String parentStock,float price,TradingBotBasic owner){
        this.shares = shares;
        this.name = parentStock;
        this.price = price;
        this.owner = owner;
        dayMade = GlobalClock.getDay();
    }

    public synchronized int getShareCount(){
        return shares;
    }

    public synchronized void removeShares(int amount){
        shares -= amount;
    }

    public synchronized void addShares(int amount){
        shares += amount;
    }

    public String getStockName(){
        return this.name;
    }

    public synchronized float getPrice(){
        return this.price;
    }

    public TradingBotBasic getOwner(){
        return this.owner;
    }

    public int getDayMade(){
     return this.dayMade;
    }




}
