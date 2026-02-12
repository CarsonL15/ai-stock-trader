package bots;
import java.util.Random;

public class TradingBotAggressive extends TradingBotBasic{

    

    
    public TradingBotAggressive(){
        
        Random R = new Random();
        personality = "Aggressive";

        idiot = R.nextInt(0,15);
        sustainability = 0;
        shortTerm = (R.nextInt(1,11) <= 7) ? 0:1;
        futureAbility = (R.nextInt(1,11) <= 7) ? 0:1;
        hypeAffect = (R.nextInt(1,11) <= 7) ? 0:1;
        trust = futureAbility = (R.nextInt(1,11) <= 5) ? 0:1;
        responseTime = 0;


        wealth = R.nextInt(1,51);
        cash = R.nextInt(100,100000) * (wealth * 10);

        cashHistory.add(cash);

    }

    public synchronized void evaluate(){ // split into 2 parts, looking at owned stocks and looking to buy stocks

        if(cashHistory.size() > 5){
            int j = cashHistory.size() - 1;
            
            double average = 0;
            
            for(int i = 0; i < 4; i++){
                average += (cashHistory.get(j) / cashHistory.get(j)) - 1;
            }

            if(average > 0){


            }
        }
 
    }

    public synchronized void BuyStockMode(){
        
    }

    

}