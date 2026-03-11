package OutsideAccess.CompactJsonObjects;

import market.StockListing;

import java.util.ArrayList;

public class CompactJsonStockListing {

    private String stockListingName;
    private String associatedCompanyName;
    private int totalSharesInExistence;
    private float lastSalePrice;
    private String hype;

    private float avgSixMonthGrowth;
    private float avgFiveYearGrowth;
    private float sixMonthGrowth;
    private float fiveYearGrowth;

    private ArrayList<Float> priceHistory = new ArrayList<>();
    private int numOfSellOrder;
    private int numOfBuyOrders;

//    private float cheapestSellOrder;
//    private float mostExpensiveBuyOrder;

    public CompactJsonStockListing(StockListing s, boolean fullInfo){
        stockListingName = s.getName();
        associatedCompanyName = s.getCompany().getName();
        totalSharesInExistence = s.getTotalShares();
        lastSalePrice = s.getLastSalePrice();

        int hypeNum = s.getHype();

        if(hypeNum > 80){
            this.hype = "Hype is overwhelmingly positive";
        }else if(hypeNum > 60){
            this.hype = "Hype is largely positive";
        }else if(hypeNum > 40){
            this.hype = "Hype is moderately positive";
        }else if(hypeNum > 20){
            this.hype = "Hype is slightly positive";
        }else if( hypeNum > 0){
            this.hype = "Hype is neutral";
        }else if(hypeNum > -20){
            this.hype = "Hype is slightly negative";
        }else if(hypeNum > -40){
            this.hype = "Hype is moderately negative";
        }else if(hypeNum > -60){
            this.hype = "Hype is largely negative";
        }else{
            this.hype = "Hype is overwhelmingly negative";
        }

        avgSixMonthGrowth = s.getAvgSixMonthGrowth();
        avgFiveYearGrowth = s.getAvgFiveYearGrowth();
        sixMonthGrowth = s.getSixMonthGrowth();
        fiveYearGrowth = s.getFiveYearGrowth();

        if(fullInfo) {
            priceHistory = s.getPriceHistory();
        }else{
            int size = s.getPriceHistory().size();
            if(size > 1){
                priceHistory = new ArrayList<>(s.getPriceHistory().subList(Math.max(0,size - 60),size));
            }
        }
        numOfSellOrder = s.getNumSellOrders();
        numOfBuyOrders = s.getNumBuyOrders();

//        cheapestSellOrder;
//        mostExpensiveBuyOrder;
    }

}
