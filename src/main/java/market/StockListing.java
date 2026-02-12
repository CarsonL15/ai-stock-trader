package market;
import java.util.ArrayList;
import java.util.Random;

import company.Company;

public class StockListing{

    private String name;
    private double marketPrice;
    private int sharesSold;
    private int totalShares;
    private int authorizedShares;
    private int companyOwnedShares;
    private Company associatedCompany;
    private double currentPrice;

    private ArrayList<Double> priceHistory = new ArrayList<>();

    private double oneMonthG;
    private double sixMonthG;
    private double oneYearG;
    private double fiveYearG;
    private double tenYearG;

    
    
    public void checkFinancial(int date){
        
    }

    public StockListing(int beginning_shares, double IPO_value, String name,Company company){
            this.name = name;
            int sharesUnsold = beginning_shares;
            this.totalShares = beginning_shares;
            this.sharesSold = 0;
            this.associatedCompany = company;

            Random R = new Random();

            double bookEval = IPO_value;
            int hype = R.nextInt(-20,100);

            companyOwnedShares = (int) (R.nextInt(0,6) * .01) * totalShares;
            sharesUnsold -= companyOwnedShares;

            // while (sharesUnsold != 0){
            //     
            // }


    }

    private void logHistory(){
        priceHistory.add(currentPrice);

    }

    public void updateGrowthValues(){

        int size = priceHistory.size();

        if(size > 2){
                double tempPrice = priceHistory.get(size - 6);
                double percent = 1;

        
            if(size > 6){
                tempPrice = priceHistory.get(size - 6);
                percent = 1;

                for(int i = size - 5; i < size; i++){
                    percent = (tempPrice / priceHistory.get(i)) - 1;
                }

                if(size > 12){

                }

            }
        }

        
    }

    public String getName(){
        return this.name;
    }



    

            
}



