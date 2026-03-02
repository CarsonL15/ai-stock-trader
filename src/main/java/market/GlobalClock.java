package market;
import java.util.Timer;
import java.util.TimerTask;

public class GlobalClock {
    

    public Timer clock;
    private static int day = 1;
    private static int year = 2000;
    private static int month = 1;
    private int monthCounter = day;

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
            monthCounter++;
            if(day > 365){
                year++;
                day = 1;
            }
            if(monthCounter > 31 && (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12)){
                month++;
                monthCounter = 1;
                Market.monthChanged(day);
            }else if(monthCounter > 30 && (month == 4 || month == 6 || month == 9 || month == 11)){
                month ++;
                monthCounter = 1;
                Market.monthChanged(day);
            }else if(monthCounter > 28 && month == 2){
                month++;
                monthCounter = 1;
                Market.monthChanged(day);
            }
            
            Market.dayChanged(day);
            System.out.println("Day has changed");
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
