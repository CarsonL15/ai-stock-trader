package OutsideAccess;

import OutsideAccess.CompactJsonObjects.CompactJsonCompany;
import OutsideAccess.CompactJsonObjects.CompactJsonStockListing;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.javalin.Javalin;
import market.Market;
import market.SetupMarket;
import market.Stock;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class APIClass {

    private static Gson g1 = new Gson();
    //private static ArrayList<SpecialBot> LLMs = new ArrayList<>();
    private static SpecialBot LLM = new SpecialBot();

    private static Type companyType = new TypeToken<CompactJsonCompany>() {}.getType();
    private static Type stockListingType = new TypeToken<CompactJsonStockListing>() {}.getType();

    private static Type stockPortfolio = new TypeToken<HashMap<String, Stock>>() {}.getType();


    public static void main(String[] args){

        SetupMarket.main(null);

        var app = Javalin.create().start(8080);

        app.get("/", ctx -> ctx.result("Server is running!"));

        app.get("/api/test", ctx -> ctx.result("GET works!"));

        app.post("/api/test", ctx -> {
            String body = ctx.body();
            ctx.result("You sent: " + body);
        });

        System.out.println("Done with api setup - server running on port 8080");
    }

    // these functions retrieve
    public static String getCompanyFinancials(String company){

        CompactJsonCompany c1 = new CompactJsonCompany(Market.getCompany(company));

        String temp = g1.toJson(c1,companyType);

        return temp;

    }

    public static String getStockListingInformation(String stockListing){

        CompactJsonStockListing s1 = new CompactJsonStockListing(Market.getStockListing(stockListing));

        String temp = g1.toJson(s1,stockListingType);

        return temp;

    }




    // these functions return the LLM's possessions
    public static String getCurrentCash(){
        return g1.toJson(LLM.getCash());
    }

    public static String getCurrentUnlistedStock(){
        return g1.toJson(LLM.getUnlistedStockHeld(),stockPortfolio);
    }

    public static String getSellOrderStock(){
        return g1.toJson(LLM.getSellOrders(),stockPortfolio);
    }

    public static String getBuyOrderStock(){
        return g1.toJson(LLM.getBuyOrders(),stockPortfolio);
    }



    public double getSome(){
        return 1;
    }

}
