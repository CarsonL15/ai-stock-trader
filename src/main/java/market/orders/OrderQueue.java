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

    public void listBuyOrder(Stock s){
        buyQueue.add(s);
        Market.updateGlobalOrderQueue(this);
    }

    public void unListBuyOrder(Stock s){
        buyQueue.remove(s);
    }

    public void listSellOrder(Stock s){
        sellQueue.add(s);
        Market.updateGlobalOrderQueue(this);
    }

    public void unListSellOrder(Stock s){
        sellQueue.remove(s);
    }

    public synchronized void checkOrders(){
        while(sellQueue.peek() !=null && buyQueue.peek() != null &&  buyQueue.peek().getPrice() >= sellQueue.peek().getPrice()){
            acquire(sellQueue.peek(),buyQueue.peek());
        }
    }



    @SuppressWarnings("DuplicatedCode")
    protected synchronized boolean acquire(Stock sellOrder, Stock buyOrder){
        synchronized(buyOrder.getOwner()) {
            synchronized (sellOrder.getOwner()) {


                if (sellOrder != null && buyOrder != null && buyOrder.getOwner().checkBuy(sellOrder.getPrice() * buyOrder.getShareCount())) {
                    if (buyOrder.getShareCount() > sellOrder.getShareCount()) {
                        Stock tempStock = new Stock(sellOrder.getShareCount(), sellOrder.getStockName(), sellOrder.getPrice(), sellOrder.getOwner());
                        buyOrder.getOwner().completeBuyOrder(tempStock);
                        sellOrder.getOwner().completeSellOrder(tempStock);

                        sellQueue.remove(sellOrder);
                        buyQueue.remove(buyOrder);
                        buyOrder.removeShares(sellOrder.getShareCount());
                        buyQueue.put(buyOrder);


                    } else if (buyOrder.getShareCount() < sellOrder.getShareCount()) {
                        Stock tempStock = new Stock(buyOrder.getShareCount(), sellOrder.getStockName(), sellOrder.getPrice(), sellOrder.getOwner());
                        buyOrder.getOwner().completeBuyOrder(tempStock);
                        sellOrder.getOwner().completeSellOrder(tempStock);

                        buyQueue.remove(sellOrder);
                        sellQueue.remove(buyOrder);
                        sellOrder.removeShares(sellOrder.getShareCount());
                        sellQueue.put(buyOrder);


                    } else {

                        buyOrder.getOwner().completeBuyOrder(sellOrder);
                        sellOrder.getOwner().completeSellOrder(sellOrder);

                        buyQueue.remove(buyOrder);
                        sellQueue.remove(sellOrder);
                    }
                    queueOwner.lastSale(sellOrder.getPrice());
                    return true;

                } else {
                    return false;
                }
            }
        }

    }

    public static class OrderQueueThread implements Runnable{

        private GlobalOrderQueue g1;

        public OrderQueueThread(GlobalOrderQueue g1){
            this.g1 = g1;
        }

        @Override
        public void run(){
            OrderQueue temp = g1.deque();
            temp.checkOrders();
        }


    }


}
