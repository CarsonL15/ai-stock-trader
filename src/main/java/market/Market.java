package market;
import java.util.*;

//import ThreadHandlers.BotThread;
import bots.TradingBotBasic;
import company.StatsCompareObject;
import company.Company;
import company.StatsComparator;
import market.orders.GlobalOrderQueue;
import market.orders.OrderQueue;
import market.orders.QueueForOrderUpdates;

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

    private static HashMap<String,Thread> botThreadStorage;
    private static ArrayList<Thread> transactionHandlers;
    private static GlobalOrderQueue orderMarket;
    private static QueueForOrderUpdates orderStorage;
    private static Thread orderQueuer = new Thread(new OrderEnqueueThread(),"OrderQueuer");
    private static Thread dayThread;
    private static Thread monthThread;

    private static long orderCount = 0;
    private static long tryOrderCount = 0;



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

    public static GlobalOrderQueue getOrders(){
        return orderMarket;
    }

    protected static void setCompanies(HashMap<String,Company> companies){
        Market.companies = companies;
    }

    protected static void setStocks(HashMap<String,StockListing> stocks){
        Market.stocks = stocks;
    }

    protected static void addStock(StockListing s){
        Market.stocks.put(s.getName(),s);
    }

    protected static void setupThreads(){
        botThreadStorage = new HashMap<>();
        UpdateMonth.initialSetupForTop200();
        orderMarket = new GlobalOrderQueue();
        orderStorage = new QueueForOrderUpdates();

//        botThreadStorage.add(new Thread(new TradingBotBasic.BotThread(new ArrayList<>(bots.subList(0,300))),"botThread1"));
//        botThreadStorage.get(0).start();



        transactionHandlers = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            transactionHandlers.add(new Thread(new OrderQueue.OrderQueueThread(orderMarket),"OrderHandler " + i));
            transactionHandlers.get(i).start();
        }
        orderQueuer.start();



    }

    public static void updateGlobalOrderQueue(OrderQueue q){
        orderStorage.enqueue(q);
    }

    public static void finishedEval(String thread){
        botThreadStorage.remove(thread);
    }

    public static void dayChanged(int day){
        
        dayThread = new Thread(new UpdateDay(day),"DayThread");
        dayThread.start();

        if(botThreadStorage.isEmpty()) {
            int section = bots.size() / 4;


            for (int i = 0; i < 4; i++) {
                botThreadStorage.put("BotThread-" + i,new Thread(new TradingBotBasic.BotThread(new ArrayList<>(bots.subList(section * i, (section * (i + 1)) - 1)),i), "BotThread-" + i));
                botThreadStorage.get("BotThread-" + i).start();
            }
        }



    }

    public static void monthChanged(int month){
        monthThread = new Thread(new UpdateMonth(),"MonthThread");
        monthThread.start();

    }

    private static ArrayList<StockListing> calculateStockSixMonthGrowth(){
        ArrayList<StatsCompareObject> top5000 = new ArrayList<>();

        for(StockListing s : stocks.values()){
            top5000.add(new StatsCompareObject(s.getSixMonthGrowth(),s,null));
        }
        return extractTop200StockListing(top5000);
    }

    private static ArrayList<StockListing> calculateStockFiveYearGrowth(){
        ArrayList<StatsCompareObject> top5000 = new ArrayList<>();

        for(StockListing s : stocks.values()){
            top5000.add(new StatsCompareObject(s.getFiveYearGrowth(),s,null));
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
                top5000.add(new StatsCompareObject(c.getCompanyBalance(), null, c));
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
        return new ArrayList<>(stocks.values());
    }

    public static ArrayList<Company> getListOfCompanies(){
        return new ArrayList<>(companies.values());
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

    public static void orderCount(){
        orderCount++;
    }

    public static void tryOrderCount(){
        tryOrderCount++;
    }

    public static class OrderEnqueueThread implements Runnable{


        @Override
        public void run(){
            while(true){

                orderMarket.enqueue(orderStorage.dequeue());
            }
        }
    }

    public static class UpdateMonth implements Runnable{



        @Override
        public void run(){
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

        public static void initialSetupForTop200(){
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
    }

    public static class UpdateDay implements Runnable{

        private int day;

        public UpdateDay(int day){
            this.day = day;
        }

        @Override
        public void run(){
            //System.out.println(orderCount + " Orders made today");
            //System.out.println(tryOrderCount + " Orders Attempted today");
            orderCount = 0;
            tryOrderCount = 0;
            for(Company c : companies.values()){
                c.updateDay(day);
            }
//            if(day % 5 == 0) { // every 5 days make a pass through the orders
//                for (StockListing s : stocks.values()) {
//                    s.updateQueue();
//                }
//            }
        }
    }

//    protected static void loopUpdate(){
//        while(true){
//            try{
//                Thread.sleep(4000);
//                System.out.print("");
//            }catch (InterruptedException e){
//
//            }
//        }
//    }
    

}






