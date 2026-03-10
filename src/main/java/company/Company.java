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

    private Float avgSixMonthCompanyGrowth = 0f;
    private Float avgFiveYearCompanyGrowth = 0f;





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
                previousRevenue = R.nextInt(0,100000000); // 0 to 100 million
                previousExpenses = R.nextInt(15000, 20000000); // 15 thousand to 20 million

                startingShares = R.nextInt(5000,10000); // 100 thousand to 1 million
            }else if(size == 2){
                previousAssets = R.nextInt(100000000,1000000000); // 100 million to 1 billion
                previousLiabilities = R.nextInt(100000000,1000000000); // 100 million to 1 billion
                previousRevenue = R.nextInt(10000,500000000); // 10 thousand to 500 million
                previousExpenses = R.nextInt(500000, 500000000); // 500 thousand to 500 million

                startingShares = R.nextInt(10000,25000); // 1 million to 2 million
            }else if(size == 3){
                previousAssets = R.nextLong(1000000000,100000000000L); // 1 billion to 100 billion
                previousLiabilities = R.nextLong(1000000000,100000000000L); // 1 billion to 100 billion
                previousRevenue = R.nextLong(1000000000,80000000000L); // 1 billion to 80 billion
                previousExpenses = R.nextLong(1000000000,100000000000L); // 1 billion to 100 billion

                startingShares = R.nextInt(25000,100000); // 1 million to 2 million
            }else if(size == 4){
                previousAssets = R.nextLong(100000000000L,5000000000000L); // 100 billion to 5 Trillion
                previousLiabilities = R.nextLong(100000000000L,5000000000000L); // 100 billion to 5 Trillion
                previousRevenue = R.nextLong(100000000000L,700000000000L); // 100 billion to 700 billion
                previousExpenses = R.nextLong(100000000000L,700000000000L); // 100 billion to 700 billion

                startingShares = R.nextInt(100000,250000); // 10 million to 20 million
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
            name = name.replace(" ","");
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
                        stockName += name.charAt(R.nextInt(i * (name.length() / 4), (1 + i) * (name.length() / 4)));
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


    public void generateFinancialReport(char type, int historicalYear, int historicalQuarter){

        long revenue = previousRevenue;
        long expenses = previousExpenses;
        long assets = previousAssets;
        long liabilities = previousLiabilities;
        long netIncome = previousNetIncome;
        long totalDebt = previousTotalDebt;

    if(type == 'q'){
        if(revenueStreak < 0){ // if business has been doing poor

            double rand = R.nextInt(1,101);
            if(rand > 90){ // 10% chance company bounces back with revenue
                double rand2 = R.nextInt(15,30);
                revenue *= (rand2 / 100) + 1;
                revenueStreak += 5;

            }else if(rand >= 50){ // 40% chance company has a +15% to -15% increase/decrease in revenue

                double rand2 = R.nextInt(-15,15);
                revenueStreak += (rand2 > 0) ? 1:-1; 
                if(rand2 != 0){
                    revenue *= (rand2 / 100) + 1;
                }

            }else if(rand <= 10){ // 10% chance company massively losses revenue
                double rand2 = R.nextInt(-30,-15);
                revenue *= (rand2 / 100) + 1;
                revenueStreak += -3;
            }else{ // 40% chance company has a -10% to +5% increase/decrease in revenue
            
                double rand2 = R.nextInt(-10,5);
                revenueStreak += (rand2 > 0) ? 1:-1;
                if(rand2 != 0){
                    revenue *= (rand2 / 100) + 1;
                }
            }

        }else{ // if business has been doing good
            double rand = R.nextInt(1,101);

            if(rand <= 3){
                double rand2 = R.nextInt(-30,-15);
                revenue *= (rand2 / 100) + 1;
                revenueStreak += -5;
            }else if(rand > 90){
                double rand2 = R.nextInt(15,30);
                revenue *= (rand2 / 100) + 1;
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

            double rand = R.nextInt(1,101);
            if(rand > 90){ // 10% chance company cuts back on spending
                double rand2 = R.nextInt(-30,-15);
                expenses *= (rand2 / 100) + 1;
                expenseStreak += 5;

            }else if(rand >= 50){ // 40% chance company has a +15% to -15% increase/decrease in expenses

                double rand2 = R.nextInt(-15,15);
                expenseStreak += (rand2 > 0) ? 1:-1; 
                if(rand2 != 0){
                    expenses *= (rand2 / 100) + 1;
                }

            }else if(rand <= 10){ // 10% chance company massively increases expenditures
                double rand2 = R.nextInt(15,30);
                expenses *= (rand2 / 100) + 1;
                expenseStreak += -3;
            }else{ // 40% chance company has a -10% to +5% increase/decrease in expenses
            
                double rand2 = R.nextInt(-10,5);
                expenseStreak += (rand2 > 0) ? 1:-1;
                if(rand2 != 0){
                    expenses *= (rand2 / 100) + 1;
                }
            }

        }else{ // if business spending has been acceptable
            double rand = R.nextInt(1,101);

            if(rand <= 3){
                double rand2 = R.nextInt(15,30);
                expenses *= (rand2 / 100) + 1;
                expenseStreak += -5;
            }else if(rand > 90){
                double rand2 = R.nextInt(-30,-15);
                expenses *= (rand2 / 100) + 1;
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



        double rand,rand2;
        if(revenueStreak > 0 && expenseStreak > 0){ // company is doing good on income and expenditures 
            rand = R.nextInt(1,15);
            rand2 = R.nextInt(-15,-1);
        }else if(revenueStreak > 0 && expenseStreak < 0){ // company is good on income but is spending a lot
            rand = R.nextInt(1,15);
            rand2 = R.nextInt(1,15);
        }else if(revenueStreak < 0 && expenseStreak > 0){ // company is doing poor on income but does not spend a lot
            rand = R.nextInt(-15,-1);
            rand2 = R.nextInt(-15,-1);
        }else{                                          // company is poor in income and is spending a lot
            rand = R.nextInt(-15,-1);
            rand2 = R.nextInt(1,15);
        }

        assets *= (rand / 100) + 1;
        liabilities *= (rand2 / 100) + 1;



        

//        if(liabilities > previousLiabilities){
//            totalDebt *= ((double) liabilities / previousLiabilities) + R.nextFloat(-5f,10f);
//        }else{
//            totalDebt *= ((double) liabilities / previousLiabilities) - R.nextFloat(-10f,5f);
//        }


        
//        if(totalDebt > liabilities){ // company has larger debt than assets
//            rand2 = R.nextInt(1,101);
//
//            if(rand2 >= 70){ // borrow more to increase revenue
//                totalDebt *= R.nextInt(1,5)/100 + 1;
//                revenueStreak++;
//                expenseStreak++;
//            }else if(rand2 <= 10){ // debt spiral
//                totalDebt *= R.nextInt(1,10);
//            }
//        }
        
//         if(revenueStreak < -1  && expenseStreak < -1 && totalDebt > liabilities * 1.25){ // company collapse
// 
//         }
        rand = R.nextInt(1,101);


        switch(size){
            case (1):
                if(revenue < 10000){
                    revenue = (long) (10000 * R.nextDouble(1,5));
                }else if (revenue > 100000000){
                    if(rand == 100){
                        size++;
                        revenue *= 1.2;
                    }else{
                        revenue = (long) (100000000.0 * R.nextDouble(.97,1.01));
                    }
                }

                if((double)revenue / expenses > 4){ // revenue is 4 times expenses
                    expenses *= 2;
                }else if((double) expenses / revenue > 4) { // expenses is 4 times revenue
                    expenses *= 0.5f;
                }

                if(expenses < 5000){
                    expenses = 5000 * R.nextInt(1,5);
                }else if(expenses > 100000000){
                    expenses = (long) (100000000.0 * R.nextDouble(.97,1.01));
                }

                if(assets < 10000){
                    assets = 10000 * R.nextInt(1,5);
                }else if(assets > 100000000){
                    if(rand == 1){
                        size++;
                        assets *= 1.2;
                    }else{
                        assets = (long) (100000000.0 * R.nextDouble(.97,1.01));
                    }
                }

                if((double)assets / liabilities > 4){ // assets is 4 times liabilities
                    liabilities *= 2;
                }else if((double) liabilities / assets > 4) { // liabilities is 4 times assets
                    liabilities *= 0.5f;
                }

                if(liabilities < 5000){
                    liabilities = 5000 * R.nextInt(1,5);
                }else if(liabilities > 100000000){
                    liabilities = (long) (100000000.0 * R.nextDouble(.97,1.01));
                }


                break;
            case (2):

                if(revenue < 500000){
                    revenue = (long) (500000 * R.nextDouble(1,5));
                }else if (revenue > 1000000000){
                    if(rand == 100){
                        size++;
                        revenue *= 1.2;
                    }else{
                        revenue = (long) (1000000000.0 * R.nextDouble(.97,1.01));
                    }
                }

                if((double)revenue / expenses > 3){ // revenue is 3 times expenses
                    expenses *= 2;
                }else if((double) expenses / revenue > 3) { // expenses is 3 times revenue
                    expenses *= 0.5f;
                }

                if(expenses < 500000){
                    expenses = 500000 * R.nextInt(1,5);
                }else if(expenses > 1000000000){
                    if(rand == 51){
                        size--;
                        expenses *= 0.5f;
                    }else {
                        expenses = (long) (1000000000.0 * R.nextDouble(.97, 1.01));
                    }
                }

                if(assets < 1000000){
                    assets = 1000000 * R.nextInt(1,5);
                }else if(assets > 1000000000){
                    if(rand == 1){
                        size++;
                        assets *= 1.2;
                    }else{
                        assets = (long) (1000000000.0 * R.nextDouble(.97,1.01));
                    }
                }

                if((double)assets / liabilities > 3){ // assets is 3 times liabilities
                    liabilities *= 2;
                }else if((double) liabilities / assets > 3) { // liabilities is 3 times assets
                    liabilities *= 0.5f;
                }

                if(liabilities < 500000){
                    liabilities = 500000 * R.nextInt(1,5);
                }else if(liabilities > 1000000000){
                    if(rand == 50){
                        size--;
                        liabilities *= 0.5f;
                    }else {
                        liabilities = (long) (1000000000.0 * R.nextDouble(.97, 1.01));
                    }
                }


                break;
            case (3):

                if(revenue < 1000000000){
                    revenue = (long) (1000000000 * R.nextDouble(1,5));
                }else if (revenue > 100000000000L){
                    if(rand == 100){
                        size++;
                        revenue *= 1.2;
                    }else{
                        revenue = (long) (100000000000L * R.nextDouble(.97,1.01));
                    }
                }

                if((double)revenue / expenses > 2){ // revenue is 2 times expenses
                    expenses *= 1.75;
                }else if((double) expenses / revenue > 2) { // expenses is 2 times revenue
                    expenses *= 0.75f;
                }

                if(expenses < 1000000000){
                    expenses = 1000000000L * R.nextInt(1,5);
                }else if(expenses > 100000000000L){
                    if(rand == 51){
                        size--;
                        expenses *= 0.5f;
                    }else {
                        expenses = (long) (100000000000L * R.nextDouble(.97, 1.01));
                    }
                }

                if(assets < 1000000000){
                    assets = 1000000000 * R.nextInt(1,5);
                }else if(assets > 100000000000L){
                    if(rand == 1){
                        size++;
                        assets *= 1.2;
                    }else{
                        assets = (long) (100000000000L * R.nextDouble(.97,1.01));
                    }
                }

                if((double)assets / liabilities > 2){ // assets is 2 times liabilities
                    liabilities *= 1.75;
                }else if((double) liabilities / assets > 2) { // liabilities is 2 times assets
                    liabilities *= 0.75f;
                }

                if(liabilities < 1000000000){
                    liabilities = 1000000000 * R.nextInt(1,5);
                }else if(liabilities > 10000000000L){
                    if(rand == 50){
                        size--;
                        liabilities *= 0.5f;
                    }else {
                        liabilities = (long) (10000000000L * R.nextDouble(.97, 1.01));
                    }
                }


                break;
            case (4):
                if(revenue < 100000000000L){
                    revenue = (long) (100000000000L * R.nextDouble(1,2));
                }else if (revenue > 700000000000L){

                    revenue = (long) (700000000000L * R.nextDouble(.97,1.01));

                }

                if((double)revenue / expenses > 2){ // revenue is 2 times expenses
                    expenses *= 1.75;
                }else if((double) expenses / revenue > 2) { // expenses is 2 times revenue
                    expenses *= 0.75f;
                }

                if(expenses < 100000000000L){
                    expenses = (long) (100000000000L * R.nextDouble(1,2));
                }else if(expenses > 700000000000L){
                    if(rand == 51){
                        size--;
                        expenses *= 0.5f;
                    }else {
                        expenses = (long) (700000000000L * R.nextDouble(.97, 1.01));
                    }
                }

                if(assets < 100000000000L){
                    assets = 100000000000L * R.nextInt(1,5);
                }else if(assets > 5000000000000L){

                    assets = (long) (5000000000000L * R.nextDouble(.97,1.01));

                }

                if((double)assets / liabilities > 2){ // assets is 2 times liabilities
                    liabilities *= 1.75;
                }else if((double) liabilities / assets > 2) { // liabilities is 2 times assets
                    liabilities *= 0.75f;
                }

                if(liabilities < 100000000000L){
                    liabilities = 100000000000L * R.nextInt(1,5);
                }else if(liabilities > 5000000000000L){
                    if(rand == 50){
                        size--;
                        liabilities *= 0.5f;
                    }else {
                        liabilities = (long) (5000000000000L * R.nextDouble(.97, 1.01));
                    }
                }


                break;
        }



        //previousTotalDebt = totalDebt;
        netIncome = revenue - expenses;
        previousAssets = assets;
        previousLiabilities = liabilities;
        previousNetIncome = netIncome;
        previousRevenue = revenue;
        previousExpenses = expenses;


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
            oldYear++;
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
            return 0f;
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
            return 0f;
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

        if(avgFiveYearCompanyGrowth != 0){
            float avgRevenue = ((float)quarterlyFinances.get(size - 19).getRevenue() / quarterlyFinances.get(size - 20).getRevenue()) - 1;
            float avgExpense = ((float)quarterlyFinances.get(size - 19).getExpenses() / quarterlyFinances.get(size - 20).getExpenses()) - 1;
            float avgAssets = ((float)quarterlyFinances.get(size - 19).getAssets() / quarterlyFinances.get(size - 20).getAssets()) - 1;
            float avgLiabilities = ((float)quarterlyFinances.get(size - 19).getLiabilities() / quarterlyFinances.get(size - 20).getLiabilities()) - 1;


            avgFiveYearCompanyGrowth -= ((avgRevenue + avgAssets) - (avgLiabilities + avgExpense) * 100);
            avgFiveYearCompanyGrowth += threeMonthGrowth;

        }else if(avgFiveYearCompanyGrowth == 0 && size >= 20){
            initialAvgGrowthValue();
        }
    }

    public ArrayList<FinancialReport> getQuarterlyFinances(){
        return this.quarterlyFinances;
    }

    public ArrayList<FinancialReport> getAnnualFinances(){
        return this.annualFinances;
    }

    public float getCompanyBalance(){
        return (float)((previousRevenue + previousAssets) - (previousExpenses + previousLiabilities)) / 1000;
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