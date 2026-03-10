package bots;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import market.Market;
import market.Stock;
import market.StockListing;

public class TradingBotPassive extends TradingBotBasic {


    public TradingBotPassive() {

        R = new Random();
        personality = "Passive";

        idiot = R.nextInt(0, 9);
        sustainability = (R.nextInt(1,11) <= 7) ? true : false;
        shortTerm = false;
        futureAbility = (R.nextInt(1, 11) <= 5) ? true : false;
        hypeAffect = (R.nextInt(1, 11) <= 5) ? true : false;
        trust = (R.nextInt(1, 11) <= 5) ? true : false;
        responseTime = R.nextInt(0,5);


        wealth = R.nextInt(1, 51);
        cash = R.nextInt(1000, 100000) * (wealth * 10);

        cashHistory.add(cash);

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
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9),stockListing), this));
                } else if (!trust && stockListing.getSixMonthGrowth() < -10) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.8, .9),stockListing), this));
                }
            }

            if (idiot <= 6 && shortTerm) {
                if (trust && stockListing.getAvgSixMonthGrowth() < -30) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9),stockListing), this));
                } else if (!trust && stockListing.getAvgSixMonthGrowth() < -10) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.8, .9),stockListing), this));
                }
            }

            if(idiot >= 7 && sustainability){
                if (trust && stockListing.getFiveYearGrowth() < -30) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9),stockListing), this));
                } else if (!trust && stockListing.getFiveYearGrowth() < -10) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.8, .9),stockListing), this));
                }
            }

            if(idiot <= 6 && sustainability){
                if (trust && stockListing.getCompany().getAvgFiveYearCompanyGrowth() < -30) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9),stockListing), this));
                } else if (!trust && stockListing.getCompany().getAvgFiveYearCompanyGrowth() < -10) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.8, .9),stockListing), this));
                }
            }

            if (hypeAffect) {
                if (stockListing.getHype() < -50) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.4, .5),stockListing), this));
                } else if (stockListing.getHype() < 0) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.5, .8),stockListing), this));
                } else if (stockListing.getHype() < 20) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9),stockListing), this));
                } else if (stockListing.getHype() > 50) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.4, 1.5),stockListing), this));
                } else if (stockListing.getHype() > 80) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.7, 1.8),stockListing), this));
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
        for (Stock s : buyOrders.values()) {

            StockListing stockListing = Market.getStockListing(s.getStockName());

            if (idiot >= 7 && shortTerm) {
                if ((stockListing.getLastSalePrice() / s.getPrice()) - 1 < -0.2) { // if the price drops by 20% or more

                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9),stockListing), this));

                } else if ((stockListing.getLastSalePrice() / s.getPrice()) - 1 > 0.2) { // if the price raises by 20% or more

                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.1, 1.4),stockListing), this));

                }
            }
            if (idiot <= 6 && shortTerm) {
                if (stockListing.getAvgSixMonthGrowth() < -20) { // if the price drops by 20% or more on average

                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9),stockListing), this));

                } else if (stockListing.getAvgSixMonthGrowth() > 20) { // if the price raises by 20% or more on average

                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.1, 1.4),stockListing), this));

                }
            }

            if (idiot >= 7 && sustainability) {
                if (stockListing.getFiveYearGrowth() < -20) { //

                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9),stockListing), this));

                } else if (stockListing.getFiveYearGrowth() > 20) { //

                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.1, 1.4),stockListing), this));

                }
            }
            if (idiot <= 6 && sustainability) {
                if (stockListing.getCompany().getAvgFiveYearCompanyGrowth() < -20) { //

                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9),stockListing), this));

                } else if (stockListing.getCompany().getAvgFiveYearCompanyGrowth() > 20) { //

                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.1, 1.4),stockListing), this));

                }
            }

            if (hypeAffect) {
                if (stockListing.getHype() < -50) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.4, .5),stockListing), this));
                } else if (stockListing.getHype() < 0) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.5, .8),stockListing), this));
                } else if (stockListing.getHype() < 20) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9),stockListing), this));
                } else if (stockListing.getHype() > 50) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.4, 1.5),stockListing), this));
                } else if (stockListing.getHype() > 80) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.7, 1.8),stockListing), this));
                }
            }


            if ((stockListing.getLastSalePrice() / s.getPrice()) - 1 <= -0.3) {
                tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.7, .8),stockListing), this));
            } else if ((stockListing.getLastSalePrice() / s.getPrice()) - 1 >= 0.3) {
                tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.1, 1.3),stockListing), this));
            }
        }

        for(Stock s : tempOrder.values()){
            listBuyOrder(s);
        }

        tempOrder.clear();
        for (Stock s : sellOrders.values()) {

            StockListing stockListing = Market.getStockListing(s.getStockName());
            if (idiot >= 7 && shortTerm) {
                if ((stockListing.getLastSalePrice() / s.getPrice()) - 1 < -0.2) { // if the price drops by 20% or more

                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9),stockListing), this));

                } else if ((stockListing.getLastSalePrice() / s.getPrice()) - 1 > 0.2) { // if the price raises by 20% or more

                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.1, 1.4),stockListing), this));

                }
            }
            if (idiot <= 6 && shortTerm) {
                if (stockListing.getAvgSixMonthGrowth() < -20) { // if the price drops by 20% or more on average

                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9),stockListing), this));

                } else if (stockListing.getAvgSixMonthGrowth() > 20) { // if the price raises by 20% or more on average

                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.1, 1.4),stockListing), this));

                }
            }
            if (idiot >= 7 && sustainability) {
                if (stockListing.getFiveYearGrowth() < -20) { //

                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9),stockListing), this));

                } else if (stockListing.getFiveYearGrowth() > 20) { //

                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.1, 1.4),stockListing), this));

                }
            }
            if (idiot <= 6 && sustainability) {
                if (stockListing.getCompany().getAvgFiveYearCompanyGrowth() < -20) { //

                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9),stockListing), this));

                } else if (stockListing.getCompany().getAvgFiveYearCompanyGrowth() > 20) { //

                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.1, 1.4),stockListing), this));

                }
            }



            if (hypeAffect) {
                if (stockListing.getHype() < -50) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.4, .5),stockListing), this));
                } else if (stockListing.getHype() < 0) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.5, .8),stockListing), this));
                } else if (stockListing.getHype() < 20) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.6, .9),stockListing), this));
                } else if (stockListing.getHype() > 50) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.4, 1.5),stockListing), this));
                } else if (stockListing.getHype() > 80) {
                    tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.7, 1.8),stockListing), this));
                }
            }


            if ((stockListing.getLastSalePrice() - 1) <= -0.3) {
                tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(.7, .8),stockListing), this));
            } else if ((stockListing.getLastSalePrice() - 1) >= 0.3) {
                tempOrder.put(s.getStockName(),new Stock(s.getShareCount(), s.getStockName(), getCompetentOrderPrice(s.getPrice() * (float) R.nextDouble(1.1, 1.3),stockListing), this));
            }


        }

        for(Stock s : tempOrder.values()){
            listSellOrder(s,true);
        }

        buySellLock.unlock();
        notifyAll();

    }

}
