package market;
import bots.TradingBotAggressive;
import bots.TradingBotBasic;
import company.Company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SetupMarket extends Market {
    


    public static void main(String args[]){

        ArrayList<TradingBotBasic> bots = new ArrayList<>();
        bots.add(new TradingBotAggressive());
        setBots(bots);
        GlobalClock time = new GlobalClock();
//        Company c1 = new Company("Glork",10);

        HashMap<String,StockListing> stocks = new HashMap<>();
        HashMap<String, Company> companies = new HashMap<>();


        companies.put("Glorck",new Company("Glorck",10));
        stocks.put(companies.get("Glorck").getStockListing().getName(),companies.get("Glorck").getStockListing());

        setCompanies(companies);
        setStocks(stocks);

        //Market mainMarket = new Market(stocks,companies,bots);

//        OrderQueue buyQueue = new OrderQueue();
        Random R = new Random();
        //StockListing s1 = c1.getStockListing();
        //TradingBotBasic bot1 = new TradingBotBasic();






        System.out.println("done with test");
        while(true){
            try{
            Thread.sleep(4000);
            System.out.print("");
            }catch (InterruptedException e){

            }
        }


        
        
    }



}
