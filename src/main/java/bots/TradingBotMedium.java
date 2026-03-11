package bots;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import market.Market;
import market.Stock;
import market.StockListing;

public class TradingBotMedium extends TradingBotBasic {


    public TradingBotMedium() {

        R = new Random();
        personality = "Medium";

        idiot = R.nextInt(0, 10);
        sustainability = (R.nextInt(1,11) <= 5) ? true : false;
        shortTerm = (R.nextInt(1, 11) <= 5) ? true : false;
        futureAbility = (R.nextInt(1, 11) <= 5) ? true : false;
        hypeAffect = (R.nextInt(1, 11) <= 5) ? true : false;
        trust = (R.nextInt(1, 11) <= 5) ? true : false;
        responseTime = R.nextInt(0,5);


        wealth = R.nextInt(1, 51);
        cash = R.nextInt(1000, 100000) * (wealth * 10);

        cashHistory.add(cash);

    }

}
