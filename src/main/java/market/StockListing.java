package market;
import java.util.Random;

public class StockListing{

    private String name;
    private double marketPrice;
    private int sharesSold;
    private int totalShares;
    private int authorizedShares;
    private int companyOwnedShares;
    private Company associatedCompany;

    
    
    public void checkFinancial(int date){
        
    }

    public StockListing(int beginning_shares, double IPO_value, String name,Company company){
            this.name = name;
            int sharesUnsold = beginning_shares;
            this.totalShares = beginning_shares;
            this.sharesSold = 0;
            this.associatedCompany = company;

            Random R = new Random();

            double bookEval = IPO_value;
            int hype = R.nextInt(-20,100);

            companyOwnedShares = (int) (R.nextInt(0,6) * .01) * totalShares;
            sharesUnsold -= companyOwnedShares;

            // while (sharesUnsold != 0){
            //     
            // }


    }

    public String getName(){
        return this.name;
    }

            
}



