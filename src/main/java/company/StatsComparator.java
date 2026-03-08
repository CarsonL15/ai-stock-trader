package company;

import java.util.Comparator;

public class StatsComparator implements Comparator<StatsCompareObject>{
    

    @Override
    public int compare(StatsCompareObject s1, StatsCompareObject s2){

        if(s1.getNum() < s2.getNum()){
            return -1;
        }else if(s1.getNum() == s2.getNum()){
            return 0;
        }else{
            return 1;
        }

        
    }

}
