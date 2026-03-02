package market;
import java.util.*;

import bots.TradingBotBasic;
import company.StatsCompareObject;
import company.Company;
import company.StatsComparator;

public class Market {

    private static String GLOBAL_INCREMENTING_NAME = "000";

    private static HashMap<String,StockListing> stocks = new HashMap<>();
    private static HashMap<String, Company> companies = new HashMap<>();
    private static ArrayList<TradingBotBasic> bots = new ArrayList<>();

    private static ArrayList<Company> bestCompanySlowGrowth = new ArrayList<>();
    private static ArrayList<Company> bestCompanyFastGrowth = new ArrayList<>();
    private static ArrayList<Company> bestAvgCompanySlowGrowth = new ArrayList<>();
    private static ArrayList<Company> bestAvgCompanyFastGrowth = new ArrayList<>();
    private static ArrayList<StockListing> bestAvgStockSlowGrowth = new ArrayList<>();
    private static ArrayList<StockListing> bestAvgStockFastGrowth = new ArrayList<>();
    private static ArrayList<StockListing> bestStockSlowGrowth = new ArrayList<>();
    private static ArrayList<StockListing> bestStockFastGrowth = new ArrayList<>();
    private static ArrayList<StockListing> mostHyped = new ArrayList<>();
    private static ArrayList<StockListing> leastHyped = new ArrayList<>();
    private static ArrayList<Company> largestCompanies = new ArrayList<>();
    private static ArrayList<StockListing> largestStocks = new ArrayList<>();



//    public Market(HashMap<String,StockListing> stocks,HashMap<String,Company> companies, ArrayList<TradingBotBasic> bots ){
//        if(!stocks.isEmpty() || !bots.isEmpty() || !companies.isEmpty()){
//            System.out.println("ERROR: duplicate market attempted to create");
//            System.exit(-420);
//        }else {
//            Market.stocks = stocks;
//            Market.companies = companies;
//            Market.bots = bots;
//        }
//    }

    protected static void setBots(ArrayList<TradingBotBasic> bots ){
        Market.bots = bots;
    }

    protected static void setCompanies(HashMap<String,Company> companies){
        Market.companies = companies;
    }

    protected static void setStocks(HashMap<String,StockListing> stocks){
        Market.stocks = stocks;
    }



    public static void dayChanged(int day){
        
        for(Company c : companies.values()){
            c.updateDay(day);
        }
    }

    public static void monthChanged(int month){
        for(StockListing s : stocks.values()){
            s.monthUpdated();
        }
        bestStockFastGrowth = calculateStockSixMonthGrowth();
        bestStockSlowGrowth = calculateStockFiveYearGrowth();
        bestAvgStockFastGrowth = calculateAvgStockSixMonthGrowth();
        bestAvgStockSlowGrowth = calculateAvgStockFiveYearGrowth();

        mostHyped = calculateHype(true);
        leastHyped = calculateHype(false);
        largestStocks = calculateLargestStocks();

        bestCompanySlowGrowth = calculateFiveYearCompanyGrowth();
        bestCompanyFastGrowth = calculateOneYearCompanyGrowth();
        bestAvgCompanySlowGrowth = calculateAvgFiveYearCompanyGrowth();
        bestAvgCompanyFastGrowth = calculateAvgSixMonthCompanyGrowth();
        largestCompanies = calculateLargestCompanies();

    }

    private static ArrayList<StockListing> calculateStockSixMonthGrowth(){
        ArrayList<StatsCompareObject> top5000 = new ArrayList<>();

        for(StockListing s : stocks.values()){
            top5000.add(new StatsCompareObject(s.getSixMonthGrowthValue(),s,null));
        }
        return extractTop200StockListing(top5000);
    }

    private static ArrayList<StockListing> calculateStockFiveYearGrowth(){
        ArrayList<StatsCompareObject> top5000 = new ArrayList<>();

        for(StockListing s : stocks.values()){
            top5000.add(new StatsCompareObject(s.getFiveYearGrowthValue(),s,null));
        }
        return extractTop200StockListing(top5000);
    }



    private static ArrayList<StockListing> calculateAvgStockSixMonthGrowth(){
        ArrayList<StatsCompareObject> top5000 = new ArrayList<>();

        for(StockListing s : stocks.values()){
            top5000.add(new StatsCompareObject(s.getAvgSixMonthGrowth(),s,null));
        }
        return extractTop200StockListing(top5000);
    }

    private static ArrayList<StockListing> calculateAvgStockFiveYearGrowth(){
        ArrayList<StatsCompareObject> top5000 = new ArrayList<>();

        for(StockListing s : stocks.values()){
            top5000.add(new StatsCompareObject(s.getAvgFiveYearGrowth(),s,null));
        }
        return extractTop200StockListing(top5000);
    }

    private static ArrayList<StockListing> calculateHype(boolean mostHyped){
        ArrayList<StatsCompareObject> top5000 = new ArrayList<>();
        if(mostHyped){
            for(StockListing s : stocks.values()){
                top5000.add(new StatsCompareObject(s.getHype(),s,null));
            }
        }else{
            for(StockListing s : stocks.values()){
                top5000.add(new StatsCompareObject(s.getHype() * -1,s,null));
            }
        }

        return extractTop200StockListing(top5000);
    }

    private static ArrayList<StockListing> calculateLargestStocks(){
        ArrayList<StatsCompareObject> top5000 = new ArrayList<>();
        for(StockListing s : stocks.values()){
            top5000.add(new StatsCompareObject(s.getLastMonthPrice() * s.getTotalShares(),s,null));
        }

        return extractTop200StockListing(top5000);
    }

    private static ArrayList<Company> calculateAvgSixMonthCompanyGrowth(){
        ArrayList<StatsCompareObject> top5000 = new ArrayList<>();
        for(Company c : companies.values()){
            top5000.add(new StatsCompareObject(c.getAvgSixMonthCompanyGrowth(),null,c));
        }

        return extractTop200Company(top5000);
    }

    private static ArrayList<Company> calculateAvgFiveYearCompanyGrowth(){
        ArrayList<StatsCompareObject> top5000 = new ArrayList<>();
        for(Company c : companies.values()){
            top5000.add(new StatsCompareObject(c.getAvgFiveYearCompanyGrowth(),null,c));
        }

        return extractTop200Company(top5000);
    }

    private static ArrayList<Company> calculateOneYearCompanyGrowth(){
        ArrayList<StatsCompareObject> top5000 = new ArrayList<>();
        for(Company c : companies.values()){
            top5000.add(new StatsCompareObject(c.getOneYearCompanyGrowth(),null,c));
        }

        return extractTop200Company(top5000);
    }

    private static ArrayList<Company> calculateFiveYearCompanyGrowth(){
        ArrayList<StatsCompareObject> top5000 = new ArrayList<>();
        for(Company c : companies.values()){
            top5000.add(new StatsCompareObject(c.getFiveYearCompanyGrowth(),null,c));
        }

        return extractTop200Company(top5000);
    }

    private static ArrayList<Company> calculateLargestCompanies(){
        ArrayList<StatsCompareObject> top5000 = new ArrayList<>();
        for(Company c : companies.values()){
            if(!c.getQuarterlyFinances().isEmpty()) {
                top5000.add(new StatsCompareObject(c.getCompanySize(), null, c));
            }
        }

        return extractTop200Company(top5000);
    }











    private static ArrayList<StockListing> extractTop200StockListing(ArrayList<StatsCompareObject> bigList){
        bigList.sort(new StatsComparator());
        ArrayList<StockListing> top200 = new ArrayList<>();

        for(int i = 0; i < 200 && i < bigList.size(); i++){
            top200.add(bigList.get(i).getStockListing());
        }
        return top200;
    }

    private static ArrayList<Company> extractTop200Company(ArrayList<StatsCompareObject> bigList){
        bigList.sort(new StatsComparator());
        ArrayList<Company> top200 = new ArrayList<>();

        for(int i = 0; i < 200; i++){
            top200.add(bigList.get(i).getCompany());
        }
        return top200;
    }



    public static ArrayList<StockListing> getTop200StockAvgGrowth(boolean avgSlowGrowth){
        if(avgSlowGrowth){
            return Market.bestAvgStockSlowGrowth;
        }else{
            return Market.bestAvgStockFastGrowth;
        }
    }

    public static ArrayList<StockListing> getTop200StockGrowth(boolean slowGrowth){
        if(slowGrowth){
            return Market.bestStockSlowGrowth;
        }else{
            return Market.bestStockFastGrowth;
        }
    }

    public static ArrayList<StockListing> getTop200Hyped(boolean negativeHype){
        if(negativeHype){
            return Market.leastHyped;
        }else{
            return Market.mostHyped;
        }
    }

    public static ArrayList<StockListing> getTop200LargestStock(){
        return Market.largestStocks;
    }

    public static ArrayList<Company> getTop200CompanyAvgGrowth(boolean avgSlowGrowth){
        if(avgSlowGrowth){
            return Market.bestAvgCompanySlowGrowth;
        }else{
            return Market.bestAvgCompanyFastGrowth;
        }
    }

    public static ArrayList<Company> getTop200CompanyGrowth(boolean slowGrowth){
        if(slowGrowth){
            return Market.bestCompanySlowGrowth;
        }else{
            return Market.bestCompanyFastGrowth;
        }
    }

    public static ArrayList<Company> getTop200LargestCompany(){
        return Market.largestCompanies;
    }

    public static StockListing getStockListing(String stockName){
        return stocks.get(stockName);
    }

    public static ArrayList<StockListing> getListOfStocks(){
        return (ArrayList<StockListing>)stocks.values();
    }

    public static Company getCompany(String companyName){
        return companies.get(companyName);
    }

    public static ArrayList<TradingBotBasic> getBotList(){
            return bots;
    }

    public static String getIncrementingName(){
        int temp = Integer.parseInt(GLOBAL_INCREMENTING_NAME);
        temp++;
        if(temp < 10){
            GLOBAL_INCREMENTING_NAME = "00" + temp;
        }else if( temp < 100){
            GLOBAL_INCREMENTING_NAME = "0" + temp;
        }else{
            GLOBAL_INCREMENTING_NAME = Integer.toString(temp);
        }
        return GLOBAL_INCREMENTING_NAME;
    }
    

}






