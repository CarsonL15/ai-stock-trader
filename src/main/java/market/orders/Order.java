package market.orders;

import market.Stock;
import market.StockListing;

public class Order {
    private StockListing associatedStockListing;
    private Stock order;

    public Order(StockListing s, Stock order){
        this.associatedStockListing = s;
        this.order = order;
    }

    public Stock getOrder(){
        return this.order;
    }

    public StockListing getStockListing(){
        return this.associatedStockListing;
    }
}
