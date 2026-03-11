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


    private ArrayList<FinancialReport> quarterlyFinances = new ArrayList<>();
    private ArrayList<FinancialReport> annualFinances = new ArrayList<>();



        public CompactJsonCompany(Company c){
            companyName = c.getName();
            stockName = c.getStockListing().getName();
            averageSixMonthCompanyGrowth = c.getAvgSixMonthCompanyGrowth();
            averageFiveYearCompanyGrowth = c.getAvgFiveYearCompanyGrowth();
            oneYearCompanyGrowth = c.getOneYearCompanyGrowth();
            fiveYearCompanyGrowth = c.getFiveYearCompanyGrowth();

            quarterlyFinances = c.getQuarterlyFinances();
            annualFinances = c.getAnnualFinances();
        }
}





