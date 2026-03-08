package market.orders;

import java.util.LinkedList;
import java.util.Queue;

public class QueueForOrderUpdates {

    public Queue<OrderQueue> orderQueue= new LinkedList<>();


    public synchronized void enqueue(OrderQueue q){
        if(orderQueue.size() > 1000){
            return;
        }else {
            orderQueue.add(q);
        }
        notifyAll();
    }

    public synchronized OrderQueue dequeue(){
        while(orderQueue.isEmpty()){
            try{
                wait();
            }catch (InterruptedException e){

            }
        }
        return orderQueue.remove();
    }
}
