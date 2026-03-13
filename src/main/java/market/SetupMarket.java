package market;
import OutsideAccess.APIClass;
import OutsideAccess.SpecialBot;
import bots.TradingBotAggressive;
import bots.TradingBotBasic;
import bots.TradingBotMedium;
import bots.TradingBotPassive;
import company.Company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class SetupMarket extends Market {
    


    public static void main(String[] args){

        HashMap<String,StockListing> stocks = new HashMap<>();
        HashMap<String, Company> companies = new HashMap<>();
        ArrayList<TradingBotBasic> bots = new ArrayList<>();
        Scanner companyNameScanner = null;
        Random R = new Random();

        File f1 = new File("./company_names.txt");
        //System.out.println(f1.exists());

        try {
            companyNameScanner = new Scanner(f1);
        }catch (FileNotFoundException e){
            System.out.println("Cannot find the file with company names");
            System.exit(0);
        }

        for(int i = 0; i < 5000; i++){
            bots.add(new TradingBotPassive());
            bots.add(new TradingBotAggressive());
            bots.add(new TradingBotMedium());
        }

        if(APIClass.getLLM() != null){
            bots.add(APIClass.getLLM());
        }


        setBots(bots);

        for(int i = 0; i < 2000; i++){
            if(!companyNameScanner.hasNextLine()){
                System.out.println("Warning less than 5000 company names detected, program may become unstable");
                break;
            }
            String name = companyNameScanner.nextLine();
            Company tempCompany = new Company(name,R.nextInt(1,100));
            companies.put(name,tempCompany);
            //stocks.put(tempCompany.getStockListing().getName(),tempCompany.getStockListing());
            addStock(tempCompany.getStockListing());
        }
        companyNameScanner.close();

        SpecialBot tempBot = APIClass.getLLM();




        setCompanies(companies);
        //setStocks(stocks);

        setupThreads();
        GlobalClock time = new GlobalClock();






        System.out.println("done with setup");
        loopUpdate();


        
        
    }



}
