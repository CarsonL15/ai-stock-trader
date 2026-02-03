package bots;
import java.util.ArrayList;
import java.util.HashMap;

import market.Stock;
import market.StockListing;

public abstract class TradingBotBasic {
    
    protected int wealth;
    protected double cash; 
    protected HashMap<String,Stock> forPurchaseStock = new HashMap<>();
    protected HashMap<String,Stock> stockHeld = new HashMap<>();
    String personality;

    protected abstract void evaluate();

    protected synchronized boolean  buyStock(Stock s, int amount, double price, TradingBotBasic sellingBot){

        
        if(!sellingBot.sellStock(s,amount,price,this)){
            return false;
        }else{
       
        if(stockHeld.containsKey(s.getStockName())){
            stockHeld.get(s.getStockName()).addShares(amount);
        }else{
            stockHeld.put(s.getStockName(),s);
        }
        cash -= amount * price;
        return true;
        }
    }

    public synchronized boolean sellStock(Stock s, int amount, double price, TradingBotBasic buyingBot){
        
        if (!checkBuy(buyingBot,price * amount)){
            return false;
        }


        
        if(!forPurchaseStock.containsKey(s.getStockName())){
            return false;
        }else{

            if(amount == s.getShareCount()){
                forPurchaseStock.remove(s.getStockName());
            }else{
                forPurchaseStock.get(s.getStockName()).removeShares(amount);
            }
            cash += price * amount;
            
            return true;
        }
    }

    public synchronized void listStock(Stock s, int amount){
        if(s.getShareCount() == amount){
            stockHeld.remove(s.getStockName());   
        }else{
            stockHeld.get(s.getStockName()).removeShares(amount);
        }
            
        if(forPurchaseStock.containsKey(s.getStockName())){
            forPurchaseStock.get(s.getStockName()).addShares(amount);
        }else{
            forPurchaseStock.put(s.getStockName(),new Stock(amount,s.getStockName()));
        }

    }

    public synchronized boolean checkBuy(TradingBotBasic buyingBot,double cost){
        if(buyingBot.getCash() < cost){
            return false;
        }else{
            return true;
        }
    }








    protected String getPersonality(){
        return this.personality;
    }

    protected HashMap<String,Stock> getPortfolio(){
        return this.stockHeld;
    }

    protected synchronized double getCash(){
        return this.cash;
    }


}
