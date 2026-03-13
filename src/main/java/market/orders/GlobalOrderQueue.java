package market.orders;

import market.Market;
import market.StockListing;

import java.util.LinkedList;
import java.util.Queue;

public class GlobalOrderQueue {

    public Queue<OrderQueue> globalQueue;


    public GlobalOrderQueue(){
        globalQueue  = new LinkedList<>();
    }

    public synchronized void newDay(){
        while(globalQueue.size() > 5000){
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }

        System.out.println("added 2000 to check orders");

        for(StockListing s : Market.getListOfStocks()){
            globalQueue.add(s.getOrderQueue());
        }
        notifyAll();
    }

    public synchronized OrderQueue deque(){
        while(globalQueue.isEmpty()){
            try {
                System.out.println("hit the bottom of the global order queue");
                wait();
            } catch (InterruptedException e) {

            }
        }

        notifyAll();
        return globalQueue.remove();

    }

}
