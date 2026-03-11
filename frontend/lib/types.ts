// Types matching the Java simulation's data shapes.
// When the Java REST API is added, these interfaces will match the JSON responses exactly.

export interface Stock {
  symbol: string;           // StockListing.getName()
  price: number;            // StockListing.getLastSalePrice()
  lastMonthPrice: number;   // StockListing.getLastMonthPrice()
  totalShares: number;      // StockListing.getTotalShares()
  hype: number;             // StockListing.getHype() — range: -100 to 100
  sixMonthGrowth: number | null;   // StockListing.getSixMonthGrowth()
  fiveYearGrowth: number | null;   // StockListing.getFiveYearGrowth()
  avgSixMonthGrowth: number;       // StockListing.getAvgSixMonthGrowth()
  avgFiveYearGrowth: number;       // StockListing.getAvgFiveYearGrowth()
  priceHistory: number[];          // StockListing.priceHistory (needs getter)
  company: Company;
}

export interface Company {
  name: string;             // Company.name (needs getName())
  size: number;             // Company.getSize() — 1=small, 2=medium, 3=large, 4=mega
  foundingYear: number;     // Company.getFoundingYear()
  quarterlyFinances: FinancialReport[];
  annualFinances: FinancialReport[];
}

export interface FinancialReport {
  revenue: number;          // FinancialReport.getRevenue()
  expenses: number;         // FinancialReport.getExpenses()
  assets: number;           // FinancialReport.getAssets()
  liabilities: number;      // FinancialReport.getLiabilities()
  netIncome: number;        // FinancialReport.getNetIncome()
  totalDebt: number;        // FinancialReport.totalDebt()
  year: number;             // FinancialReport.getYear()
  quarter: number;          // FinancialReport.getQuarter() — 1-4, or 0 for annual
}

export interface Holding {
  symbol: string;           // Stock.getStockName()
  shares: number;           // Stock.getShareCount()
  price: number;            // Stock.getPrice()
}

export interface Portfolio {
  cash: number;             // TradingBotBasic.getCash()
  holdings: Holding[];      // TradingBotBasic.getPortfolio() entries
}

export interface AgentActivity {
  id: string;
  timestamp: string;
  reasoning: string;
  action: string;           // e.g. "BUY 50 ACME @ $12.50" or "HOLD — no good opportunities"
}

export interface MarketClock {
  year: number;             // GlobalClock.getYear()
  month: number;            // GlobalClock.getMonth() (needs getter)
  day: number;              // GlobalClock.getDay()
  quarter: number;          // GlobalClock.getQuarter()
}
