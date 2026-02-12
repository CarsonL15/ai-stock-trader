package company;

import java.util.Comparator;

import market.StockListing;

public class StatsCompareObject{

    private int num;
    private StockListing s;


    public StatsCompareObject(int number, StockListing s){
        this.num = number;
        this.s = s;
    }

    public int getNum(){
        return this.num;
    }
}