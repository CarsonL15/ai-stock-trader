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

    //public abstract void evaluate();

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

    protected synchronized void unlistSellOrder(Stock s){
        Market.getStockListing(s.getStockName()).unListSellOrder(sellOrders.get(s.getStockName()));
        if(stockHeld.containsKey(s.getStockName())){
            stockHeld.get(s.getStockName()).addShares(s.getShareCount());
        }else{
            stockHeld.put(s.getStockName(),s);
        }
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

    protected synchronized void unlistBuyOrder(Stock s){
        Market.getStockListing(s.getStockName()).unListBuyOrder(buyOrders.get(s.getStockName()));
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

    public synchronized void evaluate() { // split into 2 parts, looking at owned stocks and looking to buy stocks


        while (!buySellLock.tryLock()) {
            try {
                wait();
            }catch (InterruptedException e){

            }
        }


        boolean buyStocks = true;
        boolean sellStocks = true;

        if (cashHistory.size() > 5) {
            int j = cashHistory.size() - 6;

            double average = 0;

            for (int i = 0; i < 4; i++) {
                average += (cashHistory.get(j) / cashHistory.get(j + 1)) - 1;
                j++;
            }

            if (average > .05 && cash >= wealth * 1000) {
                buyStocks = true;
                sellStocks = false;
            } else {
                buyStocks = false;
                sellStocks = true;
            }

            if (buyOrders.size() > wealth ) {
                buyStocks = false;
            }
            if (sellOrders.size() < wealth * 5) {
                sellStocks = true;
            }
        }

        if (buyStocks) {

            ArrayList<Stock> potentialBuy = new ArrayList<>();

            if (shortTerm && idiot >= 7 && hypeAffect) { // shortterm and idiot and affected by hype
                for (int i = 0; i < 3; i++) {
                    StockListing picked = Market.getTop200StockGrowth(false).get(R.nextInt(0, 200));
                    float price = getCompetentOrderPrice(picked.getLastSalePrice() * ((((float) picked.getHype() / 150) + 1) + (float) R.nextDouble(-.2, .2)),picked);

                    potentialBuy.add(new Stock(R.nextInt(1, 10) * wealth, picked.getName(), price, this));
                }
            }
            if (shortTerm && idiot >= 7 && !hypeAffect) { // short term and idiot and no hype
                for (int i = 0; i < 3; i++) {
                    StockListing picked = Market.getTop200StockGrowth(false).get(R.nextInt(0, 200));
                    float price = getCompetentOrderPrice(picked.getLastSalePrice() * (float) R.nextDouble(-.2, .2),picked);

                    potentialBuy.add(new Stock(R.nextInt(1, 10) * wealth, picked.getName(), price, this));
                }
            }
            if (shortTerm && idiot <= 6 && hypeAffect) { // short term and not idiot and hype
                for (int i = 0; i < 3; i++) {
                    StockListing picked = Market.getTop200StockAvgGrowth(false).get(R.nextInt(0, 200));
                    float price = getCompetentOrderPrice(picked.getLastSalePrice() * ((((float) picked.getHype() / 150) + 1) + (float) R.nextDouble(-.2, .2)),picked);

                    potentialBuy.add(new Stock(R.nextInt(1, 10) * wealth, picked.getName(), price, this));
                }
            }
            if (shortTerm && idiot <= 6 && !hypeAffect) { // short term not idiot and no hype
                for (int i = 0; i < 3; i++) {
                    StockListing picked = Market.getTop200StockAvgGrowth(false).get(R.nextInt(0, 200));
                    float price = getCompetentOrderPrice(picked.getLastSalePrice() * (float) R.nextDouble(-.2, .2),picked);

                    potentialBuy.add(new Stock(R.nextInt(1, 10) * wealth, picked.getName(), price, this));
                }
            }

            if(sustainability && idiot >= 7 && hypeAffect){
                for (int i = 0; i < 3; i++) {
                    StockListing picked = Market.getTop200CompanyGrowth(true).get(R.nextInt(0, 200)).getStockListing();
                    float price = getCompetentOrderPrice(picked.getLastSalePrice() * ((((float) picked.getHype() / 150) + 1) + (float) R.nextDouble(-.2, .2)),picked);

                    potentialBuy.add(new Stock(R.nextInt(1, 10) * wealth, picked.getName(), price, this));
                }
            }
            if(sustainability && idiot >= 7 && !hypeAffect){
                for (int i = 0; i < 3; i++) {
                    StockListing picked = Market.getTop200CompanyGrowth(true).get(R.nextInt(0, 200)).getStockListing();
                    float price = getCompetentOrderPrice(picked.getLastSalePrice() * (float) R.nextDouble(-.2, .2),picked);

                    potentialBuy.add(new Stock(R.nextInt(1, 10) * wealth, picked.getName(), price, this));
                }
            }

            if(sustainability && idiot <= 6 && hypeAffect){
                for (int i = 0; i < 3; i++) {
                    StockListing picked = Market.getTop200CompanyAvgGrowth(true).get(R.nextInt(0, 200)).getStockListing();
                    float price = getCompetentOrderPrice(picked.getLastSalePrice() * ((((float) picked.getHype() / 150) + 1) + (float) R.nextDouble(-.2, .2)),picked);

                    potentialBuy.add(new Stock(R.nextInt(1, 10) * wealth, picked.getName(), price, this));
                }
            }

            if(sustainability && idiot <= 6 && !hypeAffect){
                for (int i = 0; i < 3; i++) {
                    StockListing picked = Market.getTop200CompanyAvgGrowth(true).get(R.nextInt(0, 200)).getStockListing();
                    float price = getCompetentOrderPrice(picked.getLastSalePrice() * (float) R.nextDouble(-.2, .2),picked);

                    potentialBuy.add(new Stock(R.nextInt(1, 10) * wealth, picked.getName(), price, this));
                }
            }







            if (hypeAffect) { // likes hype
                for (int i = 0; i < 3; i++) {
                    StockListing picked = Market.getTop200Hyped(false).get(R.nextInt(0, 200));
                    float price = getCompetentOrderPrice(picked.getLastSalePrice() * ((((float) picked.getHype() / 150) + 1) + (float) R.nextDouble(-.2, .2)),picked);

                    potentialBuy.add(new Stock(R.nextInt(1, 10) * wealth, picked.getName(), price, this));
                }
            }
            for (int i = 0; i < 30; i++) { // random pick
                StockListing picked = Market.getListOfStocks().get(R.nextInt(0, Market.getListOfStocks().size()));
                float price = getCompetentOrderPrice(picked.getLastSalePrice() * ((((float) picked.getHype() / 150) + 1) + (float) R.nextDouble(-.2, .2)),picked);

                potentialBuy.add(new Stock(R.nextInt(1, 10) * wealth, picked.getName(), price, this));
            }

            float originalCash = cash;
            float degradingCash = cash;

            while (degradingCash /originalCash  > .6 && potentialBuy.size() > 0) {

                int rand = R.nextInt(0, potentialBuy.size());
                Stock s = potentialBuy.get(rand);
                //s = new Stock(s.getShareCount(),s.getStockName(),getCompetentOrderPrice(s.getPrice(),s.get))
                while (s.getPrice() * s.getShareCount() > cash * .8 && s.getShareCount() > 2) {
                    s.removeShares((int) (s.getShareCount() * .5));
                }
                if (!(s.getShareCount() <= 2)) { // if they can afford to list the buy order
                    listBuyOrder(s);
                    degradingCash -= s.getShareCount() * s.getPrice();
                }

                potentialBuy.remove(rand);

            }


        }

        // looks to sell stock


        HashMap<String,Stock> tempOrder = new HashMap<>();

        for (Stock s : stockHeld.values()) {




                StockListing stockListing = Market.getStockListing(s.getStockName());

                if (idiot >= 7 && shortTerm) {
                    if (trust && stockListing.getSixMonthGrowth() < -30) {
                        tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9), stockListing), this));
                    } else if (!trust && stockListing.getSixMonthGrowth() < -10) {
                        tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.8, .9), stockListing), this));
                    }
                }

                if (idiot <= 6 && shortTerm) {
                    if (trust && stockListing.getAvgSixMonthGrowth() < -30) {
                        tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9), stockListing), this));
                    } else if (!trust && stockListing.getAvgSixMonthGrowth() < -10) {
                        tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.8, .9), stockListing), this));
                    }
                }

                if (idiot >= 7 && sustainability) {
                    if (trust && stockListing.getFiveYearGrowth() < -30) {
                        tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9), stockListing), this));
                    } else if (!trust && stockListing.getFiveYearGrowth() < -10) {
                        tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.8, .9), stockListing), this));
                    }
                }

                if (idiot <= 6 && sustainability) {
                    if (trust && stockListing.getCompany().getAvgFiveYearCompanyGrowth() < -30) {
                        tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9), stockListing), this));
                    } else if (!trust && stockListing.getCompany().getAvgFiveYearCompanyGrowth() < -10) {
                        tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.8, .9), stockListing), this));
                    }
                }

                if (hypeAffect) {
                    if (stockListing.getHype() < -50) {
                        tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.4, .5), stockListing), this));
                    } else if (stockListing.getHype() < 0) {
                        tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.5, .8), stockListing), this));
                    } else if (stockListing.getHype() < 20) {
                        tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9), stockListing), this));
                    } else if (stockListing.getHype() > 50) {
                        tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.4, 1.5), stockListing), this));
                    } else if (stockListing.getHype() > 80) {
                        tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.7, 1.8), stockListing), this));
                    }
                }

        }


        for(Stock s : tempOrder.values()){
            listSellOrder(s,false);
        }

        if(sellStocks){



            if (stockHeld.size() > 3) {

                int size = (int) (stockHeld.size() / 3);
                ArrayList<Stock> tempStockOwned = new ArrayList<Stock>(stockHeld.values());


                for (int i = 0; i < size; i++) {
                    int position = R.nextInt(0 , tempStockOwned.size());
                    Stock temp = tempStockOwned.get(position);
                    StockListing stockListing = Market.getStockListing(temp.getStockName());
                    tempStockOwned.remove(position);

                    listSellOrder(new Stock(temp.getShareCount(), temp.getStockName(), getCompetentOrderPrice(stockListing.getLastSalePrice() * (float) R.nextDouble(.9, 1.1),stockListing), this), false);
                }
            }


        }

        /* This section looks at existing buy and sell orders and updates them based on their personalities/traits  starts with buy orders
           ends with sell orders */
        tempOrder.clear();
        ArrayList<Stock> removeOrders = new ArrayList<>();
        for (Stock s : buyOrders.values()) {

            StockListing stockListing = Market.getStockListing(s.getStockName());

            if(s.getShareCount() * s.getPrice() > cash){
                removeOrders.add(s);
            }else if(!(s.getDayMade() - GlobalClock.getDay() < -4)) {

                if(s.getDayMade() > GlobalClock.getDay() || s.getDayMade() - GlobalClock.getDay() < -8){
                    removeOrders.add(s);
                }else {

                    if (idiot >= 7 && shortTerm) {
                        if ((stockListing.getLastSalePrice() / s.getPrice()) - 1 < -0.2) { // if the price drops by 20% or more

                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9), stockListing), this));

                        } else if ((stockListing.getLastSalePrice() / s.getPrice()) - 1 > 0.2) { // if the price raises by 20% or more

                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.1, 1.4), stockListing), this));

                        }
                    }
                    if (idiot <= 6 && shortTerm) {
                        if (stockListing.getAvgSixMonthGrowth() < -20) { // if the price drops by 20% or more on average

                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9), stockListing), this));

                        } else if (stockListing.getAvgSixMonthGrowth() > 20) { // if the price raises by 20% or more on average

                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.1, 1.4), stockListing), this));

                        }
                    }

                    if (idiot >= 7 && sustainability) {
                        if (stockListing.getFiveYearGrowth() < -20) { //

                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9), stockListing), this));

                        } else if (stockListing.getFiveYearGrowth() > 20) { //

                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.1, 1.4), stockListing), this));

                        }
                    }
                    if (idiot <= 6 && sustainability) {
                        if (stockListing.getCompany().getAvgFiveYearCompanyGrowth() < -20) { //

                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9), stockListing), this));

                        } else if (stockListing.getCompany().getAvgFiveYearCompanyGrowth() > 20) { //

                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.1, 1.4), stockListing), this));

                        }
                    }

                    if (hypeAffect) {
                        if (stockListing.getHype() < -50) {
                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.4, .5), stockListing), this));
                        } else if (stockListing.getHype() < 0) {
                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.5, .8), stockListing), this));
                        } else if (stockListing.getHype() < 20) {
                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9), stockListing), this));
                        } else if (stockListing.getHype() > 50) {
                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.4, 1.5), stockListing), this));
                        } else if (stockListing.getHype() > 80) {
                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.7, 1.8), stockListing), this));
                        }
                    }


                    if ((stockListing.getLastSalePrice() / s.getPrice()) - 1 <= -0.3) {
                        tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.7, .8), stockListing), this));
                    } else if ((stockListing.getLastSalePrice() / s.getPrice()) - 1 >= 0.3) {
                        tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.1, 1.3), stockListing), this));
                    }
                }
            }
        }

        for(Stock s : tempOrder.values()){
            listBuyOrder(s);
        }

        for(Stock s : removeOrders){
            unlistBuyOrder(s);
        }

        tempOrder.clear();
        removeOrders.clear();
        for (Stock s : sellOrders.values()) {

            if(!(s.getDayMade() - GlobalClock.getDay() < -4)) {

                if (s.getDayMade() > GlobalClock.getDay() || s.getDayMade() - GlobalClock.getDay() < -8) {
                    removeOrders.add(s);
                } else {

                    StockListing stockListing = Market.getStockListing(s.getStockName());
                    if (idiot >= 7 && shortTerm) {
                        if ((stockListing.getLastSalePrice() / s.getPrice()) - 1 < -0.2) { // if the price drops by 20% or more

                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9), stockListing), this));

                        } else if ((stockListing.getLastSalePrice() / s.getPrice()) - 1 > 0.2) { // if the price raises by 20% or more

                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.1, 1.4), stockListing), this));

                        }
                    }
                    if (idiot <= 6 && shortTerm) {
                        if (stockListing.getAvgSixMonthGrowth() < -20) { // if the price drops by 20% or more on average

                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9), stockListing), this));

                        } else if (stockListing.getAvgSixMonthGrowth() > 20) { // if the price raises by 20% or more on average

                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.1, 1.4), stockListing), this));

                        }
                    }
                    if (idiot >= 7 && sustainability) {
                        if (stockListing.getFiveYearGrowth() < -20) { //

                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9), stockListing), this));

                        } else if (stockListing.getFiveYearGrowth() > 20) { //

                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.1, 1.4), stockListing), this));

                        }
                    }
                    if (idiot <= 6 && sustainability) {
                        if (stockListing.getCompany().getAvgFiveYearCompanyGrowth() < -20) { //

                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9), stockListing), this));

                        } else if (stockListing.getCompany().getAvgFiveYearCompanyGrowth() > 20) { //

                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.1, 1.4), stockListing), this));

                        }
                    }


                    if (hypeAffect) {
                        if (stockListing.getHype() < -50) {
                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.4, .5), stockListing), this));
                        } else if (stockListing.getHype() < 0) {
                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.5, .8), stockListing), this));
                        } else if (stockListing.getHype() < 20) {
                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9), stockListing), this));
                        } else if (stockListing.getHype() > 50) {
                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.4, 1.5), stockListing), this));
                        } else if (stockListing.getHype() > 80) {
                            tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.7, 1.8), stockListing), this));
                        }
                    }


                    if ((stockListing.getLastSalePrice() - 1) <= -0.3) {
                        tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.7, .8), stockListing), this));
                    } else if ((stockListing.getLastSalePrice() - 1) >= 0.3) {
                        tempOrder.put(s.getStockName(), new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.1, 1.3), stockListing), this));
                    }

                }

            }


        }

        for(Stock s : tempOrder.values()){
            listSellOrder(s,true);
        }

        for(Stock s : removeOrders){
            unlistSellOrder(s);
        }

        buySellLock.unlock();
        notifyAll();

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
                    if(R.nextInt(0,5) == num) {
                        b.evaluate();
                        i++;
                    }
                    //System.out.println(Thread.currentThread().getName() + " finished " + i + " evals");


            }
            System.out.println(Thread.currentThread().getName() + " finished " + i + " evals");
            //}catch (ConcurrentModificationException e){

            //}
            Market.finishedEval(Thread.currentThread().getName());
        }
    }

}
