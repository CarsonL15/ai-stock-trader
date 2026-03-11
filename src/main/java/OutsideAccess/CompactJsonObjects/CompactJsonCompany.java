package OutsideAccess.CompactJsonObjects;

import company.Company;
import company.FinancialReport;

import java.util.ArrayList;



public class CompactJsonCompany{

    private String companyName;
    private String stockName;
    private float averageSixMonthCompanyGrowth;
    private float averageFiveYearCompanyGrowth;
    private float oneYearCompanyGrowth;
    private float fiveYearCompanyGrowth;


    //private ArrayList<FinancialReport> quarterlyFinances = new ArrayList<>();
    private ArrayList<FinancialReport> annualFinances = new ArrayList<>();



        public CompactJsonCompany(Company c,boolean fullInfo){
            companyName = c.getName();
            stockName = c.getStockListing().getName();
            averageSixMonthCompanyGrowth = c.getAvgSixMonthCompanyGrowth();
            averageFiveYearCompanyGrowth = c.getAvgFiveYearCompanyGrowth();
            oneYearCompanyGrowth = c.getOneYearCompanyGrowth();
            fiveYearCompanyGrowth = c.getFiveYearCompanyGrowth();

            //quarterlyFinances = c.getQuarterlyFinances();
            if(fullInfo) {
                annualFinances = c.getAnnualFinances();
            }else{
                int size = c.getAnnualFinances().size();
                if(size > 1) {
                    annualFinances = new ArrayList<>(c.getAnnualFinances().subList(Math.max(0, size - 10), size));
                }
            }
        }
}





