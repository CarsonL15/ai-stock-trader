package company;

import market.StockListing;

public class StatsCompareObject{

    private double num;
    private StockListing s;
    private Company c;


    public StatsCompareObject(double number, StockListing stockListing, Company company){
        this.num = number;
        this.s = stockListing;
        this.c = company;
    }

    public double getNum(){
        return this.num;
    }

    public StockListing getStockListing(){
        return this.s;
    }

    public Company getCompany(){
        return this.c;
    }
}