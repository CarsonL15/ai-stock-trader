import java.util.Random;

public class Stock{

    private String name;
    private double marketPrice;
    private int sharesSold;
    private int totalShares;
    private int authorizedShares;
    private int companyOwnedShares;
    private Company associatedCompany;

    
    
    public void checkFinancial(int date){
        
    }

    public Stock(int beginning_shares, double IPO_value, String name,Company company){
            this.name = name;
            int sharesUnsold = beginning_shares;
            this.totalShares = beginning_shares;
            this.sharesSold = 0;
            this.associatedCompany = company;

            Random R = new Random();

            double bookEval = IPO_value;
            int hype = R.nextInt(-20,100);

            companyOwnedShares = R.nextInt(0,6) * totalShares;
            sharesUnsold -= companyOwnedShares;


//             for(){
// 
//             }
    }

}

