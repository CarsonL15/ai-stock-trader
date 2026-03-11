package bots;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import market.GlobalClock;
import market.Market;
import market.Stock;
import market.StockListing;

public abstract class TradingBotBasic {

    protected Lock buySellLock = new ReentrantLock();
    protected int wealth;
    protected float cash;
    protected HashMap<String,Stock> sellOrders = new HashMap<>();
    protected HashMap<String,Stock> buyOrders = new HashMap<>();
    protected HashMap<String,Stock> stockHeld = new HashMap<>();
    String personality;

    protected ArrayList<Float> cashHistory = new ArrayList<>();

    protected int idiot;
    protected boolean sustainability;
    protected boolean shortTerm;
    protected boolean futureAbility;
    protected boolean hypeAffect;
    protected boolean trust;
    protected int responseTime;

    Random R;

    public abstract void evaluate();

    public synchronized void completeBuyOrder(Stock s){
        if(buyOrders.get(s.getStockName()) != null &&  s.getPrice() <= buyOrders.get(s.getStockName()).getPrice()){
            internalCompleteBuyOrder(s);
        }

    }

    public synchronized void getBuySellLock(){
        while(!buySellLock.tryLock()){
            try {
                wait();
            }catch (InterruptedException e){

            }
        }
    }

    public synchronized void unlockBuySellLock(){
        buySellLock.unlock();
        notifyAll();
    }

    public void MonthlySalary(){
        cash += (wealth * wealth) * 1000;
    }

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


        cash -= s.getShareCount() * s.getPrice();




    }

    public synchronized void completeSellOrder(Stock s){
        if(sellOrders.get(s.getStockName()) != null &&  s.getPrice() == sellOrders.get(s.getStockName()).getPrice()){
            internalCompleteSellOrder(s);
        }
    }

    protected synchronized void internalCompleteSellOrder(Stock s){

        Stock tempSell = sellOrders.get(s.getStockName());

        if(tempSell.getShareCount() == s.getShareCount()){
            sellOrders.remove(s.getStockName());
        }else{
            sellOrders.get(s.getStockName()).removeShares(s.getShareCount());
        }

        cash += s.getShareCount() * s.getPrice();
    }

    protected synchronized void listSellOrder(Stock s, boolean reList){

        // if
//        if(!reList && !stockHeld.containsKey(s.getStockName())){ // order has been prosesee
//            return;
//        }
        // local sell order list
        if(s.getShareCount() > Market.getStockListing(s.getStockName()).getTotalShares()){
            System.out.println("hit");
        }

        if(!reList) { // if this order is being made not updated then we remove out of their personal stock
            if (s.getShareCount() == stockHeld.get(s.getStockName()).getShareCount()) {
                stockHeld.remove(s.getStockName());
            } else {
                stockHeld.get(s.getStockName()).removeShares(s.getShareCount());
            }
        }

        if(sellOrders.containsKey(s.getStockName())) {
            Market.getStockListing(s.getStockName()).unListSellOrder(sellOrders.get(s.getStockName()));
            sellOrders.remove(s.getStockName());
        }
        sellOrders.put(s.getStockName(), s);

        // global sell order list for stocklisting

        Market.getStockListing(s.getStockName()).listSellOrder(s);

    }

    protected synchronized void listBuyOrder(Stock s){
        if(buyOrders.containsKey(s.getStockName())) {
            Market.getStockListing(s.getStockName()).unListBuyOrder(buyOrders.get(s.getStockName()));
            buyOrders.remove(s.getStockName());
        }
            buyOrders.put(s.getStockName(), s);

        // global buy order list for stocklisting

        Market.getStockListing(s.getStockName()).listBuyOrder(s);
    }

    public synchronized boolean checkBuy(double cost){
        if(getCash() < cost){
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

    public int getWealth(){
        return this.wealth;
    }

    public void initialStockGiven(int amount,String stockName){
        if(stockHeld.get(stockName) != null) {
            stockHeld.get(stockName).addShares(amount);
        }else{
            stockHeld.put(stockName,new Stock(amount,stockName,0,this));
        }
    }

    public synchronized float getCompetentOrderPrice(float price, StockListing s){
        if(s != null){
            if(price < 5){ // prevents negative orders
                return s.getLastSalePrice() * R.nextFloat(0.8f,1f);
            }else if(price > s.getCompany().getCompanyBalance() * Math.pow(10,s.getCompany().getSize())){
                return s.getLastSalePrice() * R.nextFloat(.98f,1.04f);
            }else{
                return price;
            }
        }
        return 0;
    }




    public static class BotThread implements Runnable {

        ArrayList<TradingBotBasic> botList;
        Random R = new Random();
        int num;


        public BotThread(ArrayList<TradingBotBasic> botList,int num){
            this.botList = botList;
            this.num = num;
        }

        @Override
        public void run(){
            int i = 0;
            //try {


                for (TradingBotBasic b : botList) {
                    //if(R.nextInt(0,5) == num) {
                        b.evaluate();
                        i++;
                    //}
                    //System.out.println(Thread.currentThread().getName() + " finished " + i + " evals");


            }
            System.out.println(Thread.currentThread().getName() + " finished " + i + " evals");
            //}catch (ConcurrentModificationException e){

            //}
            Market.finishedEval(Thread.currentThread().getName());
        }
    }

}
