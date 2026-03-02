package market;
import java.util.ArrayList;
import java.util.Random;

import bots.TradingBotBasic;
import company.Company;

public class StockListing{

    private final String name;
    private float marketPrice;
    private int sharesSold;
    private int totalShares;
    private int authorizedShares;
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
            this.sharesSold = 0;
            this.associatedCompany = company;

            Random R = new Random();

            float bookEval = IPO_value;
            this.hype = R.nextInt(-20,100);
            companyOwnedShares = (int) (R.nextInt(0,6) * .01) * totalShares;
            sharesUnsold -= companyOwnedShares;

            while (sharesUnsold != 0){
                int botNum = R.nextInt(0,Market.getBotList().size());

                TradingBotBasic tempBot = Market.getBotList().get(botNum);

                int stockAmount = (int) Math.pow(tempBot.getWealth(),associatedCompany.getSize()) * 5;
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
            for(int i = 2;i <= 12 * (2000-company.getFoundingYear()); i++){
                int priceCheck = R.nextInt(1,101);

                if(priceCheck <= 5){
                    currentPrice *= .75;
                }else if(priceCheck <= 15){
                    currentPrice *= .85;
                }else if(priceCheck <= 30){
                    currentPrice *= .95;
                }else if(priceCheck <= 50){
                    // *= 1
                }else if(priceCheck <= 70){
                    currentPrice *= 1.05;
                }else if(priceCheck <= 85){
                    currentPrice *= 1.15;
                }else if(priceCheck <= 95){
                    currentPrice *= 1.25;
                }else{
                    currentPrice *= 1.35;
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

        int randomHype = randForHype.nextInt(0,101);

        if(randomHype == 1){
            hype -= 20;
        }else if(randomHype <= 5){
            hype -= 10;
        }else if(randomHype <= 10){
            hype -= 5;
        }else if(randomHype ==100){
            hype += 20;
        }else if(randomHype >= 95){
            hype += 10;
        }else if(randomHype >= 90){
            hype += 5;
        }

        if(hype > 100){
            hype = 100;
        }else if(hype < -100){
            hype = -100;
        }


    }

    public Float getSixMonthGrowthValue(){
        if(priceHistory.size() >= 6){
            return ((priceHistory.get(priceHistory.size() - 1) / priceHistory.get(priceHistory.size() - 6)) - 1) * 100;
        }else{
            return null;
        }
    }

    public Float getFiveYearGrowthValue(){
        if(priceHistory.size() >= 60){
            return ((priceHistory.get(priceHistory.size() - 1) / priceHistory.get(priceHistory.size() - 60)) - 1) * 100;
        }else{
            return null;
        }
    }

    public void InitialAvgGrowthValues(){

        int size = priceHistory.size();

        
            if(size >= 6){
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

                
                if(size >= 60){
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
            avgSixMonthG = avgPreviousFiveYearPercent;
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

    public void updateQueue(){
        orders.checkOrders();
    }

    public String getName(){
        return this.name;
    }

    public float getAvgSixMonthGrowth(){
        return avgSixMonthG;
    }

    public float getAvgFiveYearGrowth(){
        return avgFiveYearG;
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

            
}

