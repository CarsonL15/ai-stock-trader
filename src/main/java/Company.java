import java.util.ArrayList;
import java.util.Random;

public class Company{

    private String name;
    private Stock stock;
    private ArrayList<FinancialReport> finances;
    private int annualReportDay;
    private int quarterlyReportDay;
    private float previousRevenue;
    private float previousExpenses;
    private float previousAssets;
    private float previousLiabilities;
    private int size;

    public Company(String name,int years){
            Random R = new Random();

            annualReportDay = R.nextInt(1,366);
            quarterlyReportDay = R.nextInt(1,91);
            
            int companyStartSize = R.nextInt(1,11);

            if(companyStartSize < 3){ // simulates small company start
                size = R.nextInt(1,110);
            }else if(companyStartSize > 2 && companyStartSize < 9){ // simulates medium company start
                size = R.nextInt(110,250);
            }else if(companyStartSize == 10 && R.nextInt(1,26) == 10){ // simulates mega corp start
                size = R.nextInt(1000,2501);
            }else if(companyStartSize > 8){ // simulates large company start
                size = R.nextInt(260,510);
            }


            


            previousAssets = R.nextInt(10000000,10000000) * size;
            previousLiabilities = R.nextInt(250000,500000) * size;

            if(R.nextInt(1,101) < 5){
                previousRevenue = R.nextInt(15000,20000) * size;
            }else{
                previousRevenue = R.nextInt(25000,100000) * size;
            }

            if(R.nextInt(1,101) < 50){
                previousExpenses = R.nextInt(15000, 100000) * size;
            }else{
                previousExpenses = R.nextInt(25000,75000) * size;
            }
        

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

            stock = new Stock(R.nextInt(500,10000) * size,previousAssets - previousLiabilities,stockName,this);
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


}