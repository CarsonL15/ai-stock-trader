package bots;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import market.Market;
import market.Stock;
import market.StockListing;

public class TradingBotAggressive extends TradingBotBasic{

    

    
    public TradingBotAggressive(){
        
        R = new Random();
        personality = "Aggressive";

        idiot = R.nextInt(0,15);
        sustainability = false;
        shortTerm = (R.nextInt(1,11) <= 7) ? true:false;
        futureAbility = (R.nextInt(1,11) <= 7) ? true:false;
        hypeAffect = (R.nextInt(1,11) <= 7) ? true:false;
        trust = (R.nextInt(1,11) <= 5) ? true:false;
        responseTime = 0;


        wealth = R.nextInt(1,51);
        cash = R.nextInt(1000,100000) * (wealth * 10);

        cashHistory.add(cash);

    }

    public synchronized void evaluate(){ // split into 2 parts, looking at owned stocks and looking to buy stocks

        boolean buyStocks = true;
        boolean sellStocks = true;

        if(cashHistory.size() > 5){
            int j = cashHistory.size() - 6;
            
            double average = 0;
            
            for(int i = 0; i < 4; i++){
                average += (cashHistory.get(j) / cashHistory.get(j + 1)) - 1;
                j++;
            }

            if(average > .05 && cash >= wealth * 1000){
                buyStocks = true;
                sellStocks = false;
            }else{
                buyStocks = false;
                sellStocks = true;
            }

            if(buyOrders.size() > wealth * 100){
                buyStocks = false;
            }
            if(sellOrders.size() < wealth * 10){
                sellStocks = true;
            }
        }

        if(buyStocks){

            ArrayList<Stock> potentialBuy = new ArrayList<>();


            if(shortTerm && idiot >= 7 && hypeAffect){ // shortterm and idiot and affected by hype
                for(int i = 0; i < 3; i++) {
                    StockListing picked = Market.getTop200StockGrowth(false).get(R.nextInt(0,201));
                    float price = picked.getLastSalePrice() * ((((float)picked.getHype() / 150) + 1) + (float)R.nextDouble(-.2,.2));

                    potentialBuy.add(new Stock(R.nextInt(0,100) * wealth,picked.getName(),price,this));
                }
            }
            if(shortTerm && idiot >= 7 && !hypeAffect) { // short term and idiot and no hype
                for (int i = 0; i < 3; i++) {
                    StockListing picked = Market.getTop200StockGrowth(false).get(R.nextInt(0, 201));
                    float price = picked.getLastSalePrice() * (float) R.nextDouble(-.2, .2);

                    potentialBuy.add(new Stock(R.nextInt(0, 100) * wealth, picked.getName(), price, this));
                }
            }
            if(shortTerm && idiot <= 6 && hypeAffect){ // short term and not idiot and hype
                for(int i = 0; i < 3; i++) {
                    StockListing picked = Market.getTop200StockAvgGrowth(false).get(R.nextInt(0,201));
                    float price = picked.getLastSalePrice() * ((((float)picked.getHype() / 150) + 1) + (float)R.nextDouble(-.2,.2));

                    potentialBuy.add(new Stock(R.nextInt(0,100) * wealth,picked.getName(),price,this));
                }
            }
            if(shortTerm && idiot <= 6 && !hypeAffect){ // short term not idiot and no hype
                for(int i = 0; i < 3; i++) {
                    StockListing picked = Market.getTop200StockAvgGrowth(false).get(R.nextInt(0,201));
                    float price = picked.getLastSalePrice() * (float)R.nextDouble(-.2,.2);

                    potentialBuy.add(new Stock(R.nextInt(0,100) * wealth,picked.getName(),price,this));
                }
            }
            if(hypeAffect){ // likes hype
                for(int i = 0; i < 3; i++) {
                    StockListing picked = Market.getTop200Hyped(false).get(R.nextInt(0,201));
                    float price = picked.getLastSalePrice() * ((((float)picked.getHype() / 150) + 1) + (float)R.nextDouble(-.2,.2));

                    potentialBuy.add(new Stock(R.nextInt(0,100) * wealth,picked.getName(),price,this));
                }
            }
            for(int i = 0; i < 5; i++) { // random pick
                StockListing picked = Market.getListOfStocks().get(R.nextInt(0,Market.getListOfStocks().size()));
                float price = picked.getLastSalePrice() * ((((float)picked.getHype() / 150) + 1) + (float)R.nextDouble(-.2,.2));

                potentialBuy.add(new Stock(R.nextInt(0,100) * wealth,picked.getName(),price,this));
            }

            float originalCash = cash;
            float degradingCash = cash;

            while(originalCash / degradingCash > .6 && potentialBuy.size() > 0){

                int rand = R.nextInt(0,potentialBuy.size());
                Stock s = potentialBuy.get(rand);
                while(s.getPrice() * s.getShareCount() > cash * .8 && s.getShareCount() > 2){
                    s.removeShares((int)(s.getShareCount() * .5));
                }
                if(! (s.getShareCount() <= 2)){ // if they can afford to list the buy order
                    listBuyOrder(s);

                }

                potentialBuy.remove(rand);

            }


        }

        if(sellStocks){ // looks to sell stock
            int size = stockHeld.size();


        }

        /* This section looks at existing buy and sell orders and updates them based on their personalities/traits  starts with buy orders
           ends with sell orders */



        for (Stock s : sellOrders.values()) {

            if(idiot >= 7 && shortTerm) {
                if ((Market.getStockListing(s.getStockName()).getLastSalePrice() - 1) / s.getPrice() < -0.2) { // if the price drops by 20% or more

                    listSellOrder(new Stock(s.getShareCount(), s.getStockName(),s.getPrice() * (float)R.nextDouble(.6,.9),this),true);

                }else if((Market.getStockListing(s.getStockName()).getLastSalePrice() - 1) / s.getPrice() > 0.2){ // if the price raises by 20% or more

                    listSellOrder(new Stock(s.getShareCount(), s.getStockName(),s.getPrice() * (float)R.nextDouble(1.1,1.4),this),true);

                }
            if(idiot <= 6 && shortTerm){
                if (Market.getStockListing(s.getStockName()).getAvgSixMonthGrowth()< -0.2) { // if the price drops by 20% or more on average

                    listSellOrder(new Stock(s.getShareCount(), s.getStockName(),s.getPrice() * (float)R.nextDouble(.6,.9),this),true);

                }else if(Market.getStockListing(s.getStockName()).getLastSalePrice() > 0.2){ // if the price raises by 20% or more on average

                    listSellOrder(new Stock(s.getShareCount(), s.getStockName(),s.getPrice() * (float)R.nextDouble(1.1,1.4),this),true);

                }
            }



        }

 
    }

    public synchronized void BuyStockMode(){
        
    }

    

}