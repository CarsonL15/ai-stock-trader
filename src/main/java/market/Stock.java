package market;
public class Stock {
    private int shares;
    private String name;

    public Stock(int shares, String parentStock){
        this.shares = shares;
        this.name = parentStock;
    }

    public synchronized int getShareCount(){
        return shares;
    }

    public synchronized void removeShares(int amount){
        shares -= amount;
    }

    public synchronized void addShares(int amount){
        shares += amount;
    }

    public String getStockName(){
        return this.name;
    }
}
