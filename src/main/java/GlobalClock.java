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
    }

}
