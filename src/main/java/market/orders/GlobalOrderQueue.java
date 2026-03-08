package market.orders;

import market.Market;

import java.util.LinkedList;
import java.util.Queue;

public class GlobalOrderQueue {

    public Queue<OrderQueue> globalQueue;


    public GlobalOrderQueue(){
        globalQueue  = new LinkedList<>();
    }

    public synchronized void enqueue(OrderQueue o){
        while(globalQueue.size() > 500){
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }
        globalQueue.add(o);
        notifyAll();
    }

    public synchronized OrderQueue deque(){
        while(globalQueue.isEmpty()){
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }

        notifyAll();
        return globalQueue.remove();

    }

}
