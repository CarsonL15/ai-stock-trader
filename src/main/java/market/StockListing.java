package market;
import java.util.ArrayList;
import java.util.Random;

import bots.TradingBotBasic;
import company.Company;
import market.orders.OrderQueue;

import static java.lang.Float.NaN;

public class StockListing{

    private final String name;
    private int totalShares;
    private int companyOwnedShares;
    private Company associatedCompany;
    private float currentPrice;

    private int hype;

    private ArrayList<Float> priceHistory = new ArrayList<>();
    private OrderQueue orders = new OrderQueue(this);

    
    private float avgSixMonthG;
    private float avgFiveYearG;

    private Float avgPreviousSixMonthPercent = null;
    private Float avgPreviousFiveYearPercent = null;
    
    private Random randForHype = new Random();
    
    
    public void monthUpdated(){
        logHistory();
        updateAvgGrowthValue();
        updateHype();
    }

    public StockListing(int beginning_shares, float IPO_value, String name,Company company){
            this.name = name;
            int sharesUnsold = beginning_shares;
            this.totalShares = beginning_shares;
            this.associatedCompany = company;

            Random R = new Random();

            float bookEval = IPO_value;
            this.hype = R.nextInt(-20,100);
            companyOwnedShares = (int) (R.nextInt(0,6) * .01) * totalShares;
            sharesUnsold -= companyOwnedShares;

            while (sharesUnsold != 0){
                int botNum = R.nextInt(0,Market.getBotList().size());

                TradingBotBasic tempBot = Market.getBotList().get(botNum);

                int stockAmount = (int)(5 * Math.pow(tempBot.getWealth(),1.5));
                if(stockAmount > sharesUnsold){
                    stockAmount = sharesUnsold;
                }

                tempBot.initialStockGiven(stockAmount,name);
                sharesUnsold -= stockAmount;
                Market.getBotList().set(botNum,tempBot);
            }

                float startingPrice = (IPO_value / totalShares) * (((float)hype/100) + 1);

            if(startingPrice <= 0){
                startingPrice = R.nextFloat(1,5); // only used if company starts off failing
            }
            currentPrice = startingPrice;
            logHistory();
            float growth = associatedCompany.getCompanyBalance();
            for(int i = 2;i <= 12 * (2000-company.getFoundingYear()); i++){
                int priceCheck = R.nextInt(1,101);



                if(associatedCompany.getSize() == 1 && currentPrice > 10000){
                    currentPrice *= R.nextFloat(.9f,1.1f);
                }else if(associatedCompany.getSize() == 2 && currentPrice > 50000){
                    currentPrice *= R.nextFloat(.9f,1.1f);
                }else if(associatedCompany.getSize() == 3 && currentPrice > 75000){
                    currentPrice *= R.nextFloat(.9f,1.1f);
                }else if(associatedCompany.getSize() == 4 && currentPrice > 100000){
                    currentPrice *= R.nextFloat(.9f,1.1f);
                }else if(priceCheck <= 5){
                    currentPrice *= .75f;
                }else if(priceCheck <= 15){
                    currentPrice *= .85f;
                }else if(priceCheck <= 30){
                    currentPrice *= .95f;
                }else if(priceCheck <= 50){
                    // *= 1
                }else if(priceCheck <= 70){
                    currentPrice *= 1.05f;
                }else if(priceCheck <= 85){
                    currentPrice *= 1.15f;
                }else if(priceCheck <= 95){
                    currentPrice *= 1.25f;
                }else{
                    currentPrice *= 1.35f;
                }
                logHistory();

            }
            updateAvgGrowthValue();

    }

    public void lastSale(float soldPrice){
        currentPrice = soldPrice;
    }

    private void logHistory(){
        priceHistory.add(currentPrice);

    }

    private void updateHype(){
        if(priceHistory.size() > 4) {
            float tempEval = 0;
            float previous = 0;
            for (int i = priceHistory.size() - 4; i < priceHistory.size() - 1; i++) {
                tempEval = priceHistory.get(i) / priceHistory.get(i + 1);

                if(previous != 0){
                    if(tempEval > previous){
                        hype += 2 * (i/2);
                    }else if(tempEval < previous){
                        hype -= 2 * (i/2);
                    }
                }
                    previous = tempEval;
            }
        }

        int randomHype = randForHype.nextInt(1,101);

        if(randomHype == 1){
            hype -= 20;
        }else if(randomHype <= 5){
            hype -= 10;
        }else if(randomHype <= 25){
            hype -= 5;
        }else if(randomHype ==100){
            hype += 20;
        }else if(randomHype >= 95){
            hype += 10;
        }else if(randomHype >= 75){
            hype += 5;
        }

        if(hype > 100){
            hype = 100;
        }else if(hype < -100){
            hype = -100;
        }


    }

    public Float getSixMonthGrowth(){
        if(priceHistory.size() >= 6){
            return ((priceHistory.get(priceHistory.size() - 1) / priceHistory.get(priceHistory.size() - 6)) - 1) * 100;
        }else{
            return 0f;
        }
    }

    public Float getFiveYearGrowth(){
        if(priceHistory.size() >= 60){
            return ((priceHistory.get(priceHistory.size() - 1) / priceHistory.get(priceHistory.size() - 60)) - 1) * 100;
        }else{
            return 0f;
        }
    }

    public void InitialAvgGrowthValues(){

        int size = priceHistory.size();

        
            if(size >= 7){
                float tempPrice = priceHistory.get(size - 6);
                float percent = 0;
                

                for(int i = size - 5; i <= size - 1; i++){
                    percent += (priceHistory.get(i) / tempPrice) - 1;
                    tempPrice = priceHistory.get(i);
                }
                avgPreviousSixMonthPercent = percent;
                if(percent != 0){
                    avgSixMonthG = percent * 100;
                }else{
                    avgSixMonthG = 0;
                }

                
                if(size >= 61){
                    tempPrice = priceHistory.get(size - 60);
                    for(int i = size - 59; i <= size - 1; i++){
                    percent += (priceHistory.get(i) / tempPrice) - 1;
                    tempPrice = priceHistory.get(i);
                    }
                    avgPreviousFiveYearPercent = percent;
                    if(percent != 0){
                        avgFiveYearG = percent * 100;
                    }else{
                        avgFiveYearG = 0;
                    }
                }
            }
    }

    public void updateAvgGrowthValue(){
        int size = priceHistory.size();
        if(avgPreviousSixMonthPercent != null){
            avgPreviousSixMonthPercent -= (priceHistory.get(size - 6) /priceHistory.get(size  - 7)) - 1;
            avgPreviousSixMonthPercent += (priceHistory.get(size - 1) /priceHistory.get(size  - 2)) - 1;
            avgSixMonthG = avgPreviousSixMonthPercent;

        }else if(avgPreviousSixMonthPercent == null && size >= 6){
            InitialAvgGrowthValues();
        }

        if(avgPreviousFiveYearPercent != null){
            avgPreviousFiveYearPercent -= (priceHistory.get(size - 60) /priceHistory.get(size  - 61)) - 1;
            avgPreviousFiveYearPercent += (priceHistory.get(size - 1) /priceHistory.get(size  - 2)) - 1;
            avgFiveYearG = avgPreviousFiveYearPercent;
        }else if(avgPreviousFiveYearPercent == null && size >= 60){
            InitialAvgGrowthValues();
        }
    }

    public void listBuyOrder(Stock s){
        orders.listBuyOrder(s);
    }

    public void unListBuyOrder(Stock s){
        orders.unListBuyOrder(s);
    }

    public void listSellOrder(Stock s){
        orders.listSellOrder(s);
    }

    public void unListSellOrder(Stock s){
        orders.unListSellOrder(s);
    }

    public OrderQueue getOrderQueue(){
        return orders;
    }

    public String getName(){
        return this.name;
    }

    public float getAvgSixMonthGrowth(){
        if(Float.isNaN(avgSixMonthG)) {
            return 0;
        }else{
            return avgSixMonthG;
        }
    }

    public float getAvgFiveYearGrowth(){
        if(Float.isNaN(avgFiveYearG)) {
            return 0;
        }else{
            return avgFiveYearG;
        }

    }



    public int getHype(){
        return this.hype;
    }

    public int getTotalShares(){
        return this.totalShares;
    }

    public float getLastMonthPrice(){
        if(priceHistory.size() > 1){
            return priceHistory.get(priceHistory.size() - 1);
        }else{
            return 0;
        }
    }

    public float getLastSalePrice(){
        return currentPrice;
    }

    public ArrayList<Float> getPriceHistory(){
        return this.priceHistory;
    }

    public int getNumSellOrders(){
        return orders.getNumSellOrders();
    }


    public int getNumBuyOrders(){
        return orders.getNumBuyOrders();
    }

    public Company getCompany(){
        return associatedCompany;
    }

//    public float getMaxEstimatedValue(){
//
//    }

            
}

