package OutsideAccess.CompactJsonObjects;

import market.Stock;

public class CompactJsonStock {

    private int shares;
    private String name;
    private float price;

    public CompactJsonStock(Stock s){
        this.shares = s.getShareCount();
        this.name = s.getStockName();
        this.price = s.getPrice();
    }

    public int getShares() {
        return this.shares;
    }

    public String getName(){
        return this.name;
    }

    public float getPrice(){
        return this.price;
    }
}
