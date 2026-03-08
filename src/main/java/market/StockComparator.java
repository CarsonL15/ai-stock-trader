package market;

import java.util.Comparator;

public class StockComparator implements Comparator<Stock> {

    @Override
    public int compare(Stock o1, Stock o2) {
        if(o1.getPrice() < o2.getPrice()){
            return -1;
        }else if(o1.getPrice() == o2.getPrice()){
            return 0;
        }else{
            return 1;
        }
    }
}
