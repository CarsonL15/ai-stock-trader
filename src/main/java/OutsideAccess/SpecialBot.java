package OutsideAccess;

import bots.TradingBotBasic;
import market.Stock;

import java.util.HashMap;

public class SpecialBot extends TradingBotBasic {


    public SpecialBot(){
        cash = 1000000;
    }

    public synchronized double getCash(){
        return this.cash;
    }

    public synchronized HashMap<String, Stock> getUnlistedStockHeld(){
        getBuySellLock();
        HashMap<String,Stock> temp = this.stockHeld;
        unlockBuySellLock();
        return temp;
    }

    public synchronized HashMap<String, Stock> getSellOrders(){
        getBuySellLock();
        HashMap<String,Stock> temp = this.sellOrders;
        unlockBuySellLock();
        return temp;
    }

    public synchronized HashMap<String, Stock> getBuyOrders(){
        getBuySellLock();
        HashMap<String,Stock> temp = this.buyOrders;
        unlockBuySellLock();
        return temp;
    }


    @Override
    public void evaluate() {

    }
}
