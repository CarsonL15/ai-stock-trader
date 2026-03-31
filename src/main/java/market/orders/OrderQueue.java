package market.orders;

import market.Market;
import market.Stock;
import market.StockComparator;
import market.StockListing;

import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class OrderQueue {


    private PriorityBlockingQueue<Stock> buyQueue;
    private PriorityBlockingQueue<Stock> sellQueue;
    private StockListing queueOwner;

    public OrderQueue(StockListing s){
            buyQueue = new PriorityBlockingQueue<Stock>(10, new StockComparator().reversed());
            sellQueue = new PriorityBlockingQueue<Stock>(10, new StockComparator());
            queueOwner = s;
    }

    public  void listBuyOrder(Stock s){
        buyQueue.add(s);
        //Market.updateGlobalOrderQueue(this);
    }

    public  void unListBuyOrder(Stock s){
        buyQueue.remove(s);
    }

    public  void listSellOrder(Stock s){

        sellQueue.add(s);
        //Market.updateGlobalOrderQueue(this);
    }

    public  void unListSellOrder(Stock s){
        sellQueue.remove(s);
    }

//    public void addOrderQueueToGlobal(){
//        Market.updateGlobalOrderQueue(this);
//    }

    public synchronized void checkOrders(){
//        if(buyQueue.peek() !=null && sellQueue.peek() != null) {
//            double temp1 = buyQueue.peek().getPrice();
//
//            double temp2 = sellQueue.peek().getPrice();
//            System.out.println("");
//        }
        int j = 0;
        for(int i = 0; i < 3; i++){
            while (hasNextOrder()) {
                j++;
                acquire(sellQueue.peek(), buyQueue.peek());
                if(j > 20000){
                    if(j > 20001) {
                        break;
                    }
                    System.out.println("repeated 20000 times on stock " + queueOwner.getName());

                }
            }
        }

    }

    public synchronized boolean hasNextOrder(){
        try {
            float price1 = (buyQueue.peek() != null) ? buyQueue.peek().getPrice() : -1;
            float price2 = (sellQueue.peek() != null) ? sellQueue.peek().getPrice() : -1;
            Market.tryOrderCount();
            if (price1 != -1 && price2 != -1 && price1 >= price2) {
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException e){
            return false;
        }

    }



    @SuppressWarnings("DuplicatedCode")
    protected synchronized boolean acquire(Stock sellOrder, Stock buyOrder) {

        if(buyOrder != null) {
            buyOrder.getOwner().getBuySellLock();
        }else{
            Market.buyOrderNull();
            return false;
        }
        if(sellOrder != null) {
            sellOrder.getOwner().getBuySellLock();
        }else{
            Market.sellOrderNull();
            return false;
        }



        if (sellOrder != null && buyOrder != null && buyOrder.getOwner().checkBuy(sellOrder.getPrice() * Math.min(sellOrder.getShareCount(),buyOrder.getShareCount()))) {

            if (buyOrder.getShareCount() > sellOrder.getShareCount()) {
                Stock tempStock = new Stock(sellOrder.getShareCount(), sellOrder.getStockName(), sellOrder.getPrice(), sellOrder.getOwner());
                buyOrder.getOwner().completeBuyOrder(tempStock);
                sellOrder.getOwner().completeSellOrder(tempStock);

                //buyQueue.remove(buyOrder);
                sellQueue.remove(sellOrder);
                //buyQueue.put(new Stock(buyOrder.getShareCount() - sellOrder.getShareCount(),buyOrder.getStockName(),buyOrder.getPrice(),buyOrder.getOwner()));

                //buyQueue.remove(buyOrder);
//                       buyOrder.removeShares(sellOrder.getShareCount());
                //buyQueue.put(buyOrder);


            } else if (buyOrder.getShareCount() < sellOrder.getShareCount()) {
                Stock tempStock = new Stock(buyOrder.getShareCount(), sellOrder.getStockName(), sellOrder.getPrice(), sellOrder.getOwner());
                buyOrder.getOwner().completeBuyOrder(tempStock);
                sellOrder.getOwner().completeSellOrder(tempStock);

                buyQueue.remove(buyOrder);
                //sellQueue.remove(sellOrder);
                //sellQueue.put(new Stock(sellOrder.getShareCount() - buyOrder.getShareCount(),sellOrder.getStockName(),sellOrder.getPrice(),sellOrder.getOwner()));
                //sellQueue.remove(sellOrder);
//                       sellOrder.removeShares(sellOrder.getShareCount());
                //sellQueue.put(sellOrder);


            } else {
                Stock tempStock = new Stock(sellOrder.getShareCount(),sellOrder.getStockName(),sellOrder.getPrice(),sellOrder.getOwner());
                buyOrder.getOwner().completeBuyOrder(tempStock);
                sellOrder.getOwner().completeSellOrder(tempStock);
                buyQueue.remove(buyOrder);
                sellQueue.remove(sellOrder);


            }
            queueOwner.lastSale(sellOrder.getPrice());
            Market.orderCount();
        }else{
            buyOrder.getOwner().insufficientCash(buyOrder);
            buyQueue.remove(buyOrder);
            Market.priceMismatch();
        }

        buyOrder.getOwner().unlockBuySellLock();
        sellOrder.getOwner().unlockBuySellLock();


        return true;


    }

    public float getCheapestSellPrice(){
        Stock cheapest = sellQueue.peek();
        return cheapest != null ? cheapest.getPrice() : -1;
    }

    public float getHighestBuyPrice(){
        Stock highest = buyQueue.peek();
        return highest != null ? highest.getPrice() : -1;
    }

    // Direct market execution — bypasses the order book matching loop
    public synchronized Stock takeTopSellOrder(int maxShares){
        Stock sellOrder = sellQueue.peek();
        if(sellOrder == null) return null;

        int available = sellOrder.getShareCount();
        int traded = Math.min(maxShares, available);
        float price = sellOrder.getPrice();

        sellOrder.getOwner().getBuySellLock();

        Stock tradeStock = new Stock(traded, queueOwner.getName(), price, sellOrder.getOwner());
        sellOrder.getOwner().completeSellOrder(tradeStock);

        if(traded >= available){
            sellQueue.remove(sellOrder);
        }

        queueOwner.lastSale(price);
        Market.orderCount();

        sellOrder.getOwner().unlockBuySellLock();
        return new Stock(traded, queueOwner.getName(), price, null);
    }

    public synchronized Stock takeTopBuyOrder(int maxShares){
        Stock buyOrder = buyQueue.peek();
        if(buyOrder == null) return null;

        int available = buyOrder.getShareCount();
        int traded = Math.min(maxShares, available);
        float price = buyOrder.getPrice();

        buyOrder.getOwner().getBuySellLock();

        Stock tradeStock = new Stock(traded, queueOwner.getName(), price, buyOrder.getOwner());
        buyOrder.getOwner().completeBuyOrder(tradeStock);

        if(traded >= available){
            buyQueue.remove(buyOrder);
        }

        queueOwner.lastSale(price);
        Market.orderCount();

        buyOrder.getOwner().unlockBuySellLock();
        return new Stock(traded, queueOwner.getName(), price, null);
    }

    public int getNumSellOrders(){
        return sellQueue.size();
    }

    public int getNumBuyOrders(){
        return buyQueue.size();
    }

    public static class OrderQueueThread implements Runnable{

        private GlobalOrderQueue g1;

        public OrderQueueThread(GlobalOrderQueue g1){
            this.g1 = g1;
        }

        @Override
        public void run(){
            while(true) {
                OrderQueue temp = g1.deque();
                temp.checkOrders();
            }
        }


    }


}
