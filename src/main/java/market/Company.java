package market;
import java.util.ArrayList;
import java.util.Random;

public class Company{

    private String name;
    private StockListing stock;
    private ArrayList<FinancialReport> finances;
    private int annualReportDay;
    private int quarterlyReportDay;
    private long previousRevenue;
    private long previousExpenses;
    private long previousAssets;
    private long previousLiabilities;
    private long netIncome;
    private long totalDebt;
    private int size;

    public Company(String name,int yearsExisted){
            Random R = new Random();

            annualReportDay = R.nextInt(1,366);
            quarterlyReportDay = R.nextInt(1,91);
            
            int companyStartSize = R.nextInt(1,11);
            int startingShares = 0;

            if(companyStartSize < 3){ // simulates small company start
                size = 1;
            }else if(companyStartSize > 2 && companyStartSize < 9){ // simulates medium company start
                size = 2;
            }else if(companyStartSize == 10 && R.nextInt(1,51) == 1){ // simulates mega corp start
                size = 4;
            }else if(companyStartSize > 8){ // simulates large company start
                size = 3;
            }


            
            if(size == 1){
                previousAssets = R.nextInt(10000000,100000000); // 10 million to 100 million
                previousLiabilities = R.nextInt(5000000,50000000); // 5 million to 50 million
                previousRevenue = R.nextInt(0,20000000); // 0 to 20 million
                previousExpenses = R.nextInt(15000, 20000000); // 15 thousand to 20 million

                startingShares = R.nextInt(100000,10000000); // 100 thousand to 10 million
            }else if(size == 2){
                previousAssets = R.nextInt(100000000,1000000000); // 100 million to 1 billion
                previousLiabilities = R.nextInt(100000000,1000000000); // 100 million to 1 billion
                previousRevenue = R.nextInt(10000,500000000); // 10 thousand to 500 million
                previousExpenses = R.nextInt(500000, 500000000); // 500 thousand to 500 million

                startingShares = R.nextInt(1000000,100000000); // 1 million to 100 million
            }else if(size == 3){
                previousAssets = R.nextLong(1000000000,100000000000L); // 1 billion to 100 billion
                previousLiabilities = R.nextLong(1000000000,100000000000L); // 1 billion to 100 billion
                previousRevenue = R.nextLong(1000000000,80000000000L); // 1 billion to 80 billion
                previousExpenses = R.nextLong(1000000000,100000000000L); // 1 billion to 100 billion

                startingShares = R.nextInt(100000000,700000000); // 100 million to 700 million
            }else if(size == 4){
                previousAssets = R.nextLong(100000000000L,5000000000000L); // 100 billion to 5 Trillion
                previousLiabilities = R.nextLong(100000000000L,5000000000000L); // 100 billion to 5 Trillion
                previousRevenue = R.nextLong(100000000000L,700000000000L); // 100 billion to 700 billion
                previousExpenses = R.nextLong(100000000000L,700000000000L); // 100 billion to 700 billion

                startingShares = R.nextInt(1000000000,2100000000); // 1 billion to 2.1 billion
            }

            
            
            netIncome = previousRevenue - previousExpenses;

            int debtLuck = R.nextInt(1,11);

            if(debtLuck < 3){
                totalDebt = previousLiabilities * (R.nextInt(40,80) / 100);
            }else if(debtLuck > 8){
                totalDebt = previousLiabilities * (R.nextInt(0,50) / 100);
            }else{
                totalDebt = previousLiabilities * (R.nextInt(25,75) / 100);
            }
            

        
            // calculate unique stock name
            this.name = name;
            name = name.replace(".","");
            name = name.replace(",","");
            name = name.strip();
            
            String stockName = "";

            if(name.length() < 4){
                stockName = name;
            }else if(name.length() > 3 && name.length() < 13){
                stockName += name.charAt(0);
                stockName += name.charAt(R.nextInt(1,name.length()/2));
                stockName += name.charAt(R.nextInt(name.length() / 2,name.length() - 1));
            }else{
                for(int i = 0; i < 4;i++){
                    stockName += name.charAt(R.nextInt(1,name.length() / 4));
                }
            }
            stockName = stockName.toUpperCase();

            stock = new StockListing(startingShares,previousAssets - previousLiabilities,stockName,this);
    }

    public void generateFinancialReport(char type){

        if(finances == null){
            
        }


        
//         if(type.equals('q')){
//             
// 
// 
//             finances.add(new FinancialReport(,, GlobalClock.getYear(), name, GlobalClock.getQuarter())));
// 
//         }else if(type.equals('a')){
//             finances.add(new FinancialReport(GlobalClock.getYear(), name, GlobalClock.getQuarter())));
// 
//         }else{
//             System.out.println("invalid Financial Report type passed into financial report generator");
//             System.exit(5);
//         }

    }

    public void generateHistoricalFinancialReports(int years){

    }

    public int getSize(){
        return this.size;
    }

    public StockListing getStockListing(){
        return this.stock;
    }


}