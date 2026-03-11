package OutsideAccess;

import OutsideAccess.CompactJsonObjects.CompactJsonCompany;
import OutsideAccess.CompactJsonObjects.CompactJsonStock;
import OutsideAccess.CompactJsonObjects.CompactJsonStockListing;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import company.Company;
import io.javalin.Javalin;
import market.Market;
import market.SetupMarket;
import market.Stock;
import market.StockListing;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class APIClass {

    private static Gson g1 = new Gson();
    //private static ArrayList<SpecialBot> LLMs = new ArrayList<>();
    private static SpecialBot LLM = new SpecialBot();
    private static ArrayList<String> messages = new ArrayList<>();

    private static Type companyType = new TypeToken<CompactJsonCompany>() {}.getType();
    private static Type stockListingType = new TypeToken<CompactJsonStockListing>() {}.getType();

    private static Type stockPortfolio = new TypeToken<HashMap<String, CompactJsonStock>>() {}.getType();

    private static Type orderStock = new TypeToken<CompactJsonStock>() {}.getType();

    private static Type theString = new TypeToken<String>() {}.getType();


    public static void main(String[] args){

        SetupMarket.main(null);

        var app = Javalin.create().start(8080);

        app.get("/", ctx -> ctx.result("Server is running!"));

        app.get("/api/test", ctx -> ctx.result("GET works!"));

        /**
         * GET requests for market info
         */
        app.get("/api/getCompanyInfo", ctx -> {
            String input = ctx.body();

            ctx.result(getCompanyFinancials(input));
        });

        app.get("/api/getAllCompanyInfo", ctx -> {
            ctx.result(getAllCompanyInfo());
        });

        app.get("/api/getStockListingInfo", ctx -> {
            String input = ctx.body();

            ctx.result(getStockListingInformation(input));
        });

        app.get("/api/getAllStockListingInfo", ctx -> {
            ctx.result(getAllStockListingInfo());
        });

        /**
         * GET request for bot possessions
         */
        app.get("/api/getCash", ctx -> ctx.result(getCurrentCash()));

        app.get("/api/getUnlistedStock", ctx -> ctx.result(getCurrentlyUnlistedStock()));

        app.get("/api/getSellOrderStock", ctx -> ctx.result(getSellOrderStock()));

        app.get("/api/getBuyOrderStock", ctx -> ctx.result(getBuyOrderStock()));

        /**
         * UPDATE, these calls will return the updates of what has happend on the market with their stock ex: sold or bought
         */

        app.get("/api/getMessages", ctx -> ctx.result(getMessages()));

        /**
         * POST these calls make orders on the market
         */

        app.post("/api/placeBuyOrder", ctx -> {
            String order = ctx.body();

            ctx.result(placeBuyOrder(order));
        });

        app.post("/api/placeSellOrder", ctx -> {
            String order = ctx.body();

            ctx.result(placeSellOrder(order));
        });

        app.post("/api/removeBuyOrder", ctx -> {
            String name = ctx.body();

            ctx.result(removeBuyOrder(name));
        });

        app.post("/api/removeSellOrder", ctx -> {
            String name = ctx.body();

            ctx.result(removeSellOrder(name));
        });

        //app.get("/api/getStockHeld", ctx -> ctx.result());

        app.post("/api/test", ctx -> {
            String body = ctx.body();
            ctx.result("You sent: " + body);
        });

        System.out.println("Done with api setup - server running on port 8080");

        String companyTest = getCompanyFinancials("Onyx Enterprises");
        String stockListingTest = getStockListingInformation(Market.getCompany("Onyx Enterprises").getStockListing().getName());

        String allCompanyTest = getAllCompanyInfo();
        String allStockListingTest = getAllStockListingInfo();

        String botStockHeldRequest = getCurrentlyUnlistedStock();

        System.out.println();
    }

    // these functions retrieve info about the market
    public static String getCompanyFinancials(String company){

        CompactJsonCompany c1 = new CompactJsonCompany(Market.getCompany(company),true);

        String temp = g1.toJson(c1,companyType);

        return temp;

    }

    public static String getStockListingInformation(String stockListing){

        CompactJsonStockListing s1 = new CompactJsonStockListing(Market.getStockListing(stockListing),true);

        String temp = g1.toJson(s1,stockListingType);

        return temp;

    }

    public static String getAllCompanyInfo(){
        ArrayList<CompactJsonCompany> companies = new ArrayList<>();
        for(Company c : Market.getListOfCompanies()){
            companies.add(new CompactJsonCompany(c,false));
        }

        return g1.toJson(companies);
    }

    public static String getAllStockListingInfo(){
        ArrayList<CompactJsonStockListing> stockListings = new ArrayList<>();
        for(StockListing s : Market.getListOfStocks()){
            stockListings.add(new CompactJsonStockListing(s,false));
        }
        return g1.toJson(stockListings);
    }





    // these functions return the LLM's possessions
    public static String getCurrentCash(){
        return g1.toJson(LLM.getCash());
    }

    public static String getCurrentlyUnlistedStock(){
        return g1.toJson(LLM.getUnlistedStockHeld(),stockPortfolio);
    }

    public static String getSellOrderStock(){
        return g1.toJson(LLM.getSellOrders(),stockPortfolio);
    }

    public static String getBuyOrderStock(){
        return g1.toJson(LLM.getBuyOrders(),stockPortfolio);
    }


    // these functions notify the bots when something is sold

    public static String getMessages(){
        String temp = g1.toJson(messages);

        messages.clear();
        return temp;
    }

    public static void addMessage(String s){
        messages.add(s);
    }

    // these functions place or unlist orders

    public static String placeBuyOrder(String order){
        CompactJsonStock buyOrder = g1.fromJson(order,orderStock);

        return LLM.placeBuyOrder(buyOrder);
    }

    public static String placeSellOrder(String order){
        CompactJsonStock sellOrder = g1.fromJson(order,orderStock);

        return LLM.placeSellOrder(sellOrder);
    }

    public static String removeBuyOrder(String stockName){
        return LLM.removeBuyOrder(g1.fromJson(stockName,theString));
    }

    public static String removeSellOrder(String stockName){
        return LLM.removeSellOrder(g1.fromJson(stockName,theString));
    }


    public double getSome(){
        return 1;
    }

    public static SpecialBot getLLM(){
        return LLM;
    }

}
