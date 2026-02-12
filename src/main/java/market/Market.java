package market;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import bots.TradingBotBasic;
import company.StatsCompareObject;
import company.StatsComparator;

public class Market {
    

    private static HashMap<String,StockListing> stocks = new HashMap<>();
    private static ArrayList<TradingBotBasic> bots = new ArrayList<>();

    private static ArrayList<StockListing> bestCompanySlowGrowth = new ArrayList<>();
    private static ArrayList<StockListing> bestCompanyFastGrowth = new ArrayList<>();
    private static ArrayList<StockListing> bestStockSlowGrowth = new ArrayList<>();
    private static ArrayList<StockListing> bestStockFastGrowth = new ArrayList<>();
    private static ArrayList<StockListing> mostHyped = new ArrayList<>();
    private static ArrayList<StockListing> leastHyped = new ArrayList<>();
    private static ArrayList<StockListing> largestCompanies = new ArrayList<>();
    private static ArrayList<StockListing> largestStocks = new ArrayList<>();


    


    public static void dayChanged(){
        for(StockListing e : stocks){
            
        }
    }

    private static void CalculateStockFastGrowth(){
        LinkedList<StatsCompareObject> top = new LinkedList();

        for(StockListing e : stocks.values()){
            if(top.size() < 100){
                top.add(new StatsCompareObject(5,e));
                Collections.sort(top,new StatsComparator());
            }else{
                
            }
            
        }
    }

    

}
