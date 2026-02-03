package bots;
import java.util.Random;

public class TradingBotAggressive extends TradingBotBasic{
    
    public TradingBotAggressive(){
        
        Random R = new Random();
        personality = "Aggressive";

        wealth = R.nextInt(1,51);
        cash = R.nextInt(100,100000) * (wealth * 10);

        

    }

    public synchronized void evaluate(){

        


    }

}