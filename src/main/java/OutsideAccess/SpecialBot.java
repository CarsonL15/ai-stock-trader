package OutsideAccess;

import OutsideAccess.CompactJsonObjects.CompactJsonStock;
import bots.TradingBotBasic;
import market.Market;
import market.Stock;

import java.util.HashMap;

public class SpecialBot extends TradingBotBasic {


    public SpecialBot(){
        cash = 1000000;
        wealth = 15;
    }

    public synchronized double getCash(){
        return this.cash;
    }

    public synchronized HashMap<String, CompactJsonStock> getUnlistedStockHeld(){
        getBuySellLock();

        HashMap<String, CompactJsonStock> temp = new HashMap<>();
        for(Stock s : stockHeld.values()){
            temp.put(s.getStockName(),new CompactJsonStock(s));
        }
        unlockBuySellLock();
        return temp;
    }

    public synchronized HashMap<String, CompactJsonStock> getSellOrders(){
        getBuySellLock();

        HashMap<String, CompactJsonStock> temp = new HashMap<>();
        for(Stock s : sellOrders.values()){
            temp.put(s.getStockName(),new CompactJsonStock(s));
        }
        unlockBuySellLock();
        return temp;
    }

    public synchronized HashMap<String, CompactJsonStock> getBuyOrders(){
        getBuySellLock();

        HashMap<String, CompactJsonStock> temp = new HashMap<>();
        for(Stock s : buyOrders.values()){
            temp.put(s.getStockName(),new CompactJsonStock(s));
        }
        unlockBuySellLock();
        return temp;
    }

    public synchronized String placeBuyOrder(CompactJsonStock order){
        getBuySellLock();
        Stock s = new Stock(order.getShares(),order.getName(),order.getPrice(),this);
        String returnMessage = "";
        if(s.getPrice() <= 0){
            returnMessage = "FAILURE: Attempted to list 0 or negative price";
        }else if(s.getShareCount() <= 0){
            returnMessage = "FAILURE: Attempted to list 0 or negative shares";
        }else if(Market.getStockListing(s.getStockName()) == null) {
            returnMessage = "FAILURE: Attempted to list a order for a non existent stock";
        }else{
            listBuyOrder(s);
            returnMessage = "SUCCESS: Placed buy order";
        }
        unlockBuySellLock();
        return returnMessage;
    }

    public synchronized String placeSellOrder(CompactJsonStock order){
        getBuySellLock();
        Stock s = new Stock(order.getShares(),order.getName(),order.getPrice(),this);
        String returnMessage = "";
        if(stockHeld.containsKey(s.getStockName())){
            if(s.getShareCount() > stockHeld.get(s.getStockName()).getShareCount()){
                returnMessage = "FAILURE: Attempted to list more shares than currently owned";
            }else if(s.getShareCount() <= 0){
                returnMessage = "FAILURE: Attempted to list 0 or negative shares";
            }else if(s.getPrice() <= 0){
                returnMessage = "FAILURE: Attempted to list 0 or negative price";
            }else {
                listSellOrder(s, false);
                returnMessage = "SUCCESS: Placed sell order";
            }
        }else{
            returnMessage = "FAILURE: " + s.getStockName() + " does not exist in your portfolio";
        }
        unlockBuySellLock();
        return returnMessage;
    }

    public synchronized String removeBuyOrder(String name){
        getBuySellLock();
        String returnMessage = "";
        if(buyOrders.containsKey(name)){
            Market.getStockListing(name).unListBuyOrder(buyOrders.get(name));
            returnMessage = "SUCCESS: Removed buy order for stock " + name;
        }else{
            returnMessage = "FAILURE: Buy Order for stock + " + name + " does not exists";
        }
        unlockBuySellLock();
        return returnMessage;
    }

    public synchronized String removeSellOrder(String name){
        getBuySellLock();
        String returnMessage = "";
        if(sellOrders.containsKey(name)){
            Market.getStockListing(name).unListSellOrder(sellOrders.get(name));
            returnMessage = "SUCCESS: Removed sell order for stock " + name;
        }else{
            returnMessage = "FAILURE: Sell Order for stock + " + name + " does not exists";
        }
        unlockBuySellLock();
        return returnMessage;
    }

    @Override
    protected synchronized void internalCompleteSellOrder(Stock s){

        Stock tempSell = sellOrders.get(s.getStockName());

        if(tempSell.getShareCount() == s.getShareCount()){
            sellOrders.remove(s.getStockName());
        }else{
            sellOrders.get(s.getStockName()).removeShares(s.getShareCount());
        }
        APIClass.addMessage("UPDATE: your " + s.getStockName() + " stock has sold " + s.getShareCount() + " shares for " + s.getShareCount() * s.getPrice() + " dollars");
        cash += s.getShareCount() * s.getPrice();
    }

    @Override
    protected synchronized void  internalCompleteBuyOrder(Stock s){


        Stock tempBuy = buyOrders.get(s.getStockName());

        if(tempBuy.getShareCount() == s.getShareCount()){
            buyOrders.remove(s.getStockName());
        }else{
            buyOrders.get(s.getStockName()).removeShares(s.getShareCount());
        }

        if(stockHeld.containsKey(s.getStockName())){
            stockHeld.get(s.getStockName()).addShares(s.getShareCount());
        }else{
            stockHeld.put(s.getStockName(),new Stock(s.getShareCount(),s.getStockName(),0,this));
        }

        APIClass.addMessage("UPDATE: you bought " + s.getShareCount() + " shares for " + s.getStockName() + " stock which cost " + s.getShareCount() * s.getPrice() + " dollars");
        cash -= s.getShareCount() * s.getPrice();

    }


    @Override
    public void evaluate() { // required but not used, the bot should externally evaluate the cost or the player should

    }
}
