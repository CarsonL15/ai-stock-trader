public class FinancialReport{
    private double assets;
    private double liabilities;
    private char type;
    private int year;
    private int quarter;
    private String company;
    private double netRevenue;
    private double expenses;

    
    public FinancialReport(double assets, double liabilities, int year, String company,int quarter){
        this.assets = assets;
        this.liabilities = liabilities;
        this.year = year;
        this.company = company;
        this.quarter = quarter;
    }

    public double calculateNetValue(){
        return (Math.round((assets - liabilities) * 100)) / 100;
    }

    public double calculateNetProfit(){
        return (Math.round((netRevenue - expenses) * 100)) / 100;
    }

    

}