package market;
import java.util.Timer;
import java.util.TimerTask;

public class GlobalClock {
    

    public Timer clock;
    private static int day = 1;
    private static int year = 2000;

    public GlobalClock(){
        clock = new Timer();
        clock.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                updateDate();}
        
            },10000,10000);
    }


    public void updateDate(){
            day++;
            if(day > 365){
                year++;
                day = 1;
            }
            Market.dayChanged();
    }

    public static int getYear(){
        return year;
    }

    public static int getDay(){
        return day;
    }

    public  static int getQuarter(){
        if (day >= 1 && day <= 90){
            return 1;
        }else if(day >= 91 && day <= 181){
            return 2;
        }else if(day >= 182 && day <= 273){
            return 3;
        }else{
            return 4;
        }
    }

}
