import java.util.Timer;
import java.util.TimerTask;

public class GlobalClock {
    

    public Timer clock;
    private int day = 1;
    private int year = 2000;

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

    public int getYear(){
        return this.year;
    }

    public int getDay(){
        return this.day;
    }

    public int getQuarter(){
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
