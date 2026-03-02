package company;
import java.util.ArrayList;
import java.util.Random;

import market.GlobalClock;
import market.Market;
import market.StockListing;

public class Company{

    private String name;
    private int foundingYear;
    private StockListing stock;
    private ArrayList<FinancialReport> quarterlyFinances = new ArrayList<>();
    private ArrayList<FinancialReport> annualFinances = new ArrayList<>();
    private int annualReportDay;
    private int quarterlyReportDay;
    private long previousRevenue;
    private long previousExpenses;
    private long previousAssets;
    private long previousLiabilities;
    private long previousNetIncome;
    private long previousTotalDebt;
    private int size;

    private int revenueStreak = 0;
    private int expenseStreak = 0;
    private int debtStreak;
    Random R = new Random();

    private Float sixMonthCompanyGrowth = null;
    private Float avgSixMonthCompanyGrowth = null;
    private Float fiveYearCompanyGrowth = null;
    private Float avgFiveYearCompanyGrowth = null;





    public Company(String name,int yearsExisted){
            
            foundingYear = 2000-yearsExisted;
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

                startingShares = R.nextInt(100000,1000000); // 100 thousand to 1 million
            }else if(size == 2){
                previousAssets = R.nextInt(100000000,1000000000); // 100 million to 1 billion
                previousLiabilities = R.nextInt(100000000,1000000000); // 100 million to 1 billion
                previousRevenue = R.nextInt(10000,500000000); // 10 thousand to 500 million
                previousExpenses = R.nextInt(500000, 500000000); // 500 thousand to 500 million

                startingShares = R.nextInt(1000000,2000000); // 1 million to 2 million
            }else if(size == 3){
                previousAssets = R.nextLong(1000000000,100000000000L); // 1 billion to 100 billion
                previousLiabilities = R.nextLong(1000000000,100000000000L); // 1 billion to 100 billion
                previousRevenue = R.nextLong(1000000000,80000000000L); // 1 billion to 80 billion
                previousExpenses = R.nextLong(1000000000,100000000000L); // 1 billion to 100 billion

                startingShares = R.nextInt(1000000,2000000); // 1 million to 2 million
            }else if(size == 4){
                previousAssets = R.nextLong(100000000000L,5000000000000L); // 100 billion to 5 Trillion
                previousLiabilities = R.nextLong(100000000000L,5000000000000L); // 100 billion to 5 Trillion
                previousRevenue = R.nextLong(100000000000L,700000000000L); // 100 billion to 700 billion
                previousExpenses = R.nextLong(100000000000L,700000000000L); // 100 billion to 700 billion

                startingShares = R.nextInt(10000000,20000000); // 10 million to 20 million
            }

            
            
            previousNetIncome = previousRevenue - previousExpenses;

            int debtLuck = R.nextInt(1,11);

            if(debtLuck < 3){
                previousTotalDebt = previousLiabilities * (R.nextInt(40,80) / 100);
            }else if(debtLuck > 8){
                previousTotalDebt = previousLiabilities * (R.nextInt(0,50) / 100);
            }else{
                previousTotalDebt = previousLiabilities * (R.nextInt(25,75) / 100);
            }
            

        
            // calculate unique stock name
            this.name = name;
            name = name.replace(".","");
            name = name.replace(",","");
            name = name.strip();
            
            String stockName = "";
            int j = 0;
            while(stockName == "" || Market.getStockListing(stockName) != null) {
                if (name.length() < 4) {
                    stockName = name;
                } else if (name.length() > 3 && name.length() < 13) {
                    stockName += name.charAt(0);
                    stockName += name.charAt(R.nextInt(1, name.length() / 2));
                    stockName += name.charAt(R.nextInt(name.length() / 2, name.length() - 1));
                } else {
                    for (int i = 0; i < 4; i++) {
                        stockName += name.charAt(R.nextInt(1, name.length() / 4));
                    }
                }
                stockName = stockName.toUpperCase();
                if(j > 50){
                    stockName = Market.getIncrementingName();
                }else{
                    j++;
                }
            }

            stock = new StockListing(startingShares,previousAssets - previousLiabilities,stockName,this);

            generateHistoricalFinancialReports(yearsExisted);
            updateAvgGrowthValue();
        }



    public void updateDay(int day){
        if(quarterlyReportDay == day){
            generateFinancialReport('q',0,0);
        }
        if(annualReportDay == day){
            generateFinancialReport('a',0,0);
        }
    }

    public void generateFinancialReport(char type,int historicalYear,int historicalQuarter){

        long revenue = previousRevenue;
        long expenses = previousExpenses;
        long assets = previousAssets;
        long liabilities = previousLiabilities;
        long netIncome = previousNetIncome;
        long totalDebt = previousTotalDebt;

    if(type == 'q'){
        if(revenueStreak < 0){ // if business has been doing poor

            int rand = R.nextInt(1,101);
            if(rand > 90){ // 10% chance company bounces back with revenue
                revenue *= (R.nextInt(15,30)/100 + 1);
                revenueStreak += 5;

            }else if(rand >= 50){ // 40% chance company has a +15% to -15% increase/decrease in revenue

                double rand2 = R.nextInt(-15,15);
                revenueStreak += (rand2 > 0) ? 1:-1; 
                if(rand2 != 0){
                    revenue *= (rand2 / 100) + 1;
                }

            }else if(rand <= 10){ // 10% chance company massively losses revenue
                revenue *= (R.nextInt(-30,-15) /100) + 1;
                revenueStreak += -3;
            }else{ // 40% chance company has a -10% to +5% increase/decrease in revenue
            
                double rand2 = R.nextInt(-10,5);
                revenueStreak += (rand2 > 0) ? 1:-1;
                if(rand2 != 0){
                    revenue *= rand2 / 100;
                }
            }

        }else{ // if business has been doing good
            int rand = R.nextInt(1,101);

            if(rand <= 3){
                revenue *= (R.nextInt(-30,-15) / 100) + 1;
                revenueStreak += -5;
            }else if(rand > 90){
                revenue *= (R.nextInt(15,30) / 100) + 1;
                revenueStreak += 3;
            }else if(rand >= 50){
                
                double rand2 = R.nextInt(-10,15);
                if(rand2 < 0){
                    revenueStreak--;
                }else if(rand2 > 10){
                    revenueStreak++;
                } 
                if(rand2 != 0){
                    revenue *= (rand2 / 100) + 1;
                }
            }else{
                double rand2 = R.nextInt(-7,7);
                if(rand2 != 0){
                    revenue *= (rand2 / 100) + 1;
                }
            }
        }

        if(expenseStreak < 0){ // if business has been spending significant amount of money

            int rand = R.nextInt(1,101);
            if(rand > 90){ // 10% chance company cuts back on spending
                expenses *= (R.nextInt(-30,-15)/100 + 1);
                expenseStreak += 5;

            }else if(rand >= 50){ // 40% chance company has a +15% to -15% increase/decrease in expenses

                double rand2 = R.nextInt(-15,15);
                expenseStreak += (rand2 > 0) ? 1:-1; 
                if(rand2 != 0){
                    expenses *= (rand2 / 100) + 1;
                }

            }else if(rand <= 10){ // 10% chance company massively increases expenditures
                expenses *= (R.nextInt(15,30) /100) + 1;
                expenseStreak += -3;
            }else{ // 40% chance company has a -10% to +5% increase/decrease in expenses
            
                double rand2 = R.nextInt(-10,5);
                expenseStreak += (rand2 > 0) ? 1:-1;
                if(rand2 != 0){
                    expenses *= rand2 / 100;
                }
            }

        }else{ // if business spending has been acceptable
            int rand = R.nextInt(1,101);

            if(rand <= 3){
                expenses *= (R.nextInt(15,30) / 100) + 1;
                expenseStreak += -5;
            }else if(rand > 90){
                expenses *= (R.nextInt(-30,-15) / 100) + 1;
                expenseStreak += 3;
            }else if(rand >= 50){
                
                double rand2 = R.nextInt(-10,15);
                if(rand2 < 0){
                    expenseStreak++;
                }else if(rand2 > 10){
                    expenseStreak--;
                } 
                if(rand2 != 0){
                    expenses *= (rand2 / 100) + 1;
                }
            }else{
                double rand2 = R.nextInt(-7,7);
                if(rand2 != 0){
                    expenses *= (rand2 / 100) + 1;
                }
            }
        }

        previousNetIncome = revenue - expenses;

        if(revenueStreak > 0 && expenseStreak > 0){ // company is doing good on income and expenditures 
            assets *= (R.nextInt(1,15) / 100) + 1;
            liabilities *= (R.nextInt(-15,-1) / 100) + 1;
        }else if(revenueStreak > 0 && expenseStreak < 0){ // company is good on income but is spending a lot
            assets *= (R.nextInt(1,15) / 100) + 1;
            liabilities *= (R.nextInt(1,15) / 100) + 1;
        }else if(revenueStreak < 0 && expenseStreak > 0){ // company is doing poor on income but does not spend a lot
            assets *= (R.nextInt(-15,-1) / 100) + 1;
            liabilities *= (R.nextInt(-15,-1) / 100) + 1;
        }else{                                          // company is poor in income and is spending a lot
            assets *= (R.nextInt(-15,-1) / 100) + 1;
            liabilities *= (R.nextInt(1,15) / 100) + 1;
        }

        

        if(liabilities > previousLiabilities){
            totalDebt *= ((double) liabilities / previousLiabilities) + R.nextInt(-5,10)/100;
        }else{
            totalDebt *= ((double) liabilities / previousLiabilities) - R.nextInt(-10,5)/100;
        } 

        
        if(totalDebt > liabilities){ // company has larger debt than assets
            int rand2 = R.nextInt(1,101);

            if(rand2 >= 70){ // borrow more to increase revenue
                totalDebt *= R.nextInt(1,5)/100 + 1;
                revenueStreak++;
                expenseStreak++;
            }else if(rand2 <= 10){ // debt spiral
                totalDebt *= R.nextInt(1,10);
            }
        }
        
//         if(revenueStreak < -1  && expenseStreak < -1 && totalDebt > liabilities * 1.25){ // company collapse
// 
//         }

        if(historicalYear == 0 && historicalQuarter == 0){
            quarterlyFinances.add(new FinancialReport(assets,liabilities,revenue,expenses,netIncome,totalDebt,GlobalClock.getYear(),name,GlobalClock.getQuarter()));
        }else{
            quarterlyFinances.add(new FinancialReport(assets,liabilities,revenue,expenses,netIncome,totalDebt,historicalYear,name,historicalQuarter));
        }


    }else{

        
        int j = quarterlyFinances.size() - 1;

        revenue = 0;
        expenses = 0;
        assets = 0;
        liabilities = 0;
        netIncome = 0;
        totalDebt = 0;
        
        for(int i = 0; i < 4; i++){
            
            FinancialReport temp = quarterlyFinances.get(j);
            
            
            revenue += temp.getRevenue();
            expenses += temp.getExpenses();
            assets += temp.getAssets();
            liabilities += temp.getLiabilities();
            netIncome += temp.getNetIncome();
            totalDebt += temp.totalDebt();
            j--;
            if(j < 0){
                break;
            }


        }
        if(historicalYear == 0){
            annualFinances.add(new FinancialReport(assets,liabilities,revenue,expenses,netIncome,totalDebt,GlobalClock.getYear(),name,0));
        }else{
            annualFinances.add(new FinancialReport(assets,liabilities,revenue,expenses,netIncome,totalDebt,historicalYear,name,0));
        }
    }  

    }

    public void generateHistoricalFinancialReports(int years){

        

        int oldYear = GlobalClock.getYear() - years;
        for(int i = 0; i < years; i++){

            for(int j = 1; j <= 4; j++){
                generateFinancialReport('q',oldYear,j);
            }
            generateFinancialReport('a',oldYear,0);
        }

    }

    public Float getOneYearCompanyGrowth(){
        if(annualFinances.size() >= 2){

            float rev = ((float)annualFinances.get(annualFinances.size() - 1).getRevenue() / annualFinances.get(annualFinances.size() - 2).getRevenue()) - 1;
            float exp = ((float)annualFinances.get(annualFinances.size() - 1).getExpenses() / annualFinances.get(annualFinances.size() - 2).getExpenses()) - 1;
            float ass = ((float)annualFinances.get(annualFinances.size() - 1).getAssets() / annualFinances.get(annualFinances.size() - 2).getAssets()) - 1;
            float lib = ((float)annualFinances.get(annualFinances.size() - 1).getLiabilities() / annualFinances.get(annualFinances.size() - 2).getLiabilities()) - 1;

            return ((rev + ass) - (exp + lib)) * 100;
        }else{
            return null;
        }
    }

    public Float getFiveYearCompanyGrowth(){
        if(annualFinances.size() >= 20){
            float rev = ((float)annualFinances.get(annualFinances.size() - 1).getRevenue() / annualFinances.get(annualFinances.size() - 5).getRevenue()) - 1;
            float exp = ((float)annualFinances.get(annualFinances.size() - 1).getExpenses() / annualFinances.get(annualFinances.size() - 5).getExpenses()) - 1;
            float ass = ((float)annualFinances.get(annualFinances.size() - 1).getAssets() / annualFinances.get(annualFinances.size() - 5).getAssets()) - 1;
            float lib = ((float)annualFinances.get(annualFinances.size() - 1).getLiabilities() / annualFinances.get(annualFinances.size() - 5).getLiabilities()) - 1;

            return ((rev + ass) - (exp + lib)) * 100;
        }else{
            return null;
        }
    }

    public void initialAvgGrowthValue(){

        int size = quarterlyFinances.size();

            if(size >= 20){
                float avgRevenue = 0;
                float avgExpense = 0;
                float avgAssets = 0;
                float avgLiabilities = 0;

                for(int i = size - 19; i < size; i++){
                    avgRevenue += ((float)quarterlyFinances.get(i).getRevenue() / quarterlyFinances.get(i - 1).getRevenue()) - 1;
                    avgExpense += ((float)quarterlyFinances.get(i).getExpenses() / quarterlyFinances.get(i - 1).getExpenses()) - 1;
                    avgAssets += ((float)quarterlyFinances.get(i).getAssets() / quarterlyFinances.get(i - 1).getAssets()) - 1;
                    avgLiabilities += ((float)quarterlyFinances.get(i).getLiabilities() / quarterlyFinances.get(i - 1).getLiabilities()) - 1;
                }

                avgFiveYearCompanyGrowth = ((avgRevenue + avgAssets) - (avgLiabilities + avgExpense) * 100);

            }

    }

    // REMINDER: R1 --- R2 covers six months with 2 reports if r1 and r2 are quarterly reports
    public void updateAvgGrowthValue(){
        int size = quarterlyFinances.size();
        float threeMonthGrowth = 0;
        if(size >= 3){

            float avgRevenue = ((float)quarterlyFinances.get(size - 1).getRevenue() / quarterlyFinances.get(size - 2).getRevenue()) - 1;
            float avgExpense = ((float)quarterlyFinances.get(size - 1).getExpenses() / quarterlyFinances.get(size - 2).getExpenses()) - 1;
            float avgAssets = ((float)quarterlyFinances.get(size - 1).getAssets() / quarterlyFinances.get(size - 2).getAssets()) - 1;
            float avgLiabilities = ((float)quarterlyFinances.get(size - 1).getLiabilities() / quarterlyFinances.get(size - 2).getLiabilities()) - 1;

            threeMonthGrowth = ((avgRevenue + avgAssets) - (avgLiabilities + avgExpense) * 100);

            avgRevenue = ((float)quarterlyFinances.get(size - 2).getRevenue() / quarterlyFinances.get(size - 3).getRevenue()) - 1;
            avgExpense = ((float)quarterlyFinances.get(size - 2).getExpenses() / quarterlyFinances.get(size - 3).getExpenses()) - 1;
            avgAssets = ((float)quarterlyFinances.get(size - 2).getAssets() / quarterlyFinances.get(size - 3).getAssets()) - 1;
            avgLiabilities = ((float)quarterlyFinances.get(size - 2).getLiabilities() / quarterlyFinances.get(size - 3).getLiabilities()) - 1;

            avgSixMonthCompanyGrowth = ((avgRevenue + avgAssets) - (avgLiabilities + avgExpense) * 100) + threeMonthGrowth;

        }

        if(avgFiveYearCompanyGrowth != null){
            float avgRevenue = ((float)quarterlyFinances.get(size - 19).getRevenue() / quarterlyFinances.get(size - 20).getRevenue()) - 1;
            float avgExpense = ((float)quarterlyFinances.get(size - 19).getExpenses() / quarterlyFinances.get(size - 20).getExpenses()) - 1;
            float avgAssets = ((float)quarterlyFinances.get(size - 19).getAssets() / quarterlyFinances.get(size - 20).getAssets()) - 1;
            float avgLiabilities = ((float)quarterlyFinances.get(size - 19).getLiabilities() / quarterlyFinances.get(size - 20).getLiabilities()) - 1;


            avgFiveYearCompanyGrowth -= ((avgRevenue + avgAssets) - (avgLiabilities + avgExpense) * 100);
            avgFiveYearCompanyGrowth += threeMonthGrowth;

        }else if(avgFiveYearCompanyGrowth == null && size >= 20){
            initialAvgGrowthValue();
        }
    }

    public ArrayList<FinancialReport> getQuarterlyFinances(){
        return this.quarterlyFinances;
    }

    public ArrayList<FinancialReport> getAnnualFinances(){
        return this.annualFinances;
    }

    public double getCompanySize(){
        return (double)((previousRevenue + previousAssets) - (previousExpenses + previousLiabilities)) / 1000;
    }

    public float getAvgSixMonthCompanyGrowth(){
        return avgSixMonthCompanyGrowth;
    }

    public float getAvgFiveYearCompanyGrowth(){
        return avgFiveYearCompanyGrowth;
    }

    public int getSize(){
        return this.size;
    }

    public int getFoundingYear(){
        return this.foundingYear;
    }

    public StockListing getStockListing(){
        return this.stock;
    }
}