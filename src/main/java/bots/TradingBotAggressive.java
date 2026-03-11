package bots;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import market.Market;
import market.Stock;
import market.StockListing;

public class TradingBotAggressive extends TradingBotBasic {


    public TradingBotAggressive() {

        R = new Random();
        personality = "Aggressive";

        idiot = R.nextInt(0, 15);
        sustainability = false;
        shortTerm = (R.nextInt(1, 11) <= 7) ? true : false;
        futureAbility = (R.nextInt(1, 11) <= 7) ? true : false;
        hypeAffect = (R.nextInt(1, 11) <= 7) ? true : false;
        trust = (R.nextInt(1, 11) <= 3) ? true : false;
        responseTime = 0;


        wealth = R.nextInt(1, 51);
        cash = R.nextInt(1000, 100000) * (wealth * 10);

        cashHistory.add(cash);

    }

}