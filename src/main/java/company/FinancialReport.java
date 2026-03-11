package company;
public class FinancialReport{
    private long assets;
    private long liabilities;
    private long revenue;
    private long expenses;
    private long netIncome;
    private int year;
    private int quarter;
    private String company;

    
    public FinancialReport(long assets, long liabilities, long revenue,long expenses, long netIncome, int year, String company,int quarter){
        this.assets = assets;
        this.liabilities = liabilities;
        this.revenue = revenue;
        this.expenses = expenses;
        this.netIncome = netIncome;
        this.year = year;
        this.company = company;
        this.quarter = quarter;
    }

    public long getAssets(){
        return this.assets;
    }

    public long getLiabilities(){
        return this.liabilities;
    }

    public long getRevenue(){
        return this.revenue;
    }

    public long getExpenses(){
        return this.expenses;
    }

    public long getNetIncome(){
        return this.netIncome;
    }

    public int getYear(){
        return this.year;
    }

    public int getQuarter(){
        return this.quarter;
    }
    
    public String getCompany(){
        return this.company;
    }

}