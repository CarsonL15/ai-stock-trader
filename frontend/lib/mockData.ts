import {
  Stock,
  Company,
  FinancialReport,
  Portfolio,
  AgentActivity,
  MarketClock,
} from "./types";

// --- Helpers for generating realistic mock data ---

function generatePriceHistory(
  startPrice: number,
  months: number
): number[] {
  const history: number[] = [startPrice];
  let price = startPrice;
  for (let i = 1; i < months; i++) {
    const roll = Math.random() * 100;
    if (roll < 5) price *= 0.75;
    else if (roll < 15) price *= 0.85;
    else if (roll < 30) price *= 0.95;
    else if (roll < 50) price *= 1.0;
    else if (roll < 70) price *= 1.05;
    else if (roll < 85) price *= 1.15;
    else if (roll < 95) price *= 1.25;
    else price *= 1.35;
    history.push(parseFloat(price.toFixed(2)));
  }
  return history;
}

function generateFinancialReports(
  companyName: string,
  startYear: number,
  size: number
): { quarterly: FinancialReport[]; annual: FinancialReport[] } {
  const quarterly: FinancialReport[] = [];
  const annual: FinancialReport[] = [];
  const baseRevenue = size * 500_000;
  const baseExpenses = size * 400_000;
  const baseAssets = size * 2_000_000;
  const baseLiabilities = size * 800_000;

  for (let year = startYear; year <= 2000; year++) {
    let annualRevenue = 0;
    let annualExpenses = 0;
    for (let q = 1; q <= 4; q++) {
      const growthFactor = 1 + (year - startYear) * 0.02 + Math.random() * 0.1;
      const revenue = Math.round(baseRevenue * growthFactor * (0.9 + Math.random() * 0.2));
      const expenses = Math.round(baseExpenses * growthFactor * (0.9 + Math.random() * 0.2));
      const assets = Math.round(baseAssets * growthFactor);
      const liabilities = Math.round(baseLiabilities * growthFactor * (0.9 + Math.random() * 0.3));
      annualRevenue += revenue;
      annualExpenses += expenses;

      quarterly.push({
        revenue,
        expenses,
        assets,
        liabilities,
        netIncome: revenue - expenses,
        totalDebt: Math.round(liabilities * 0.6),
        year,
        quarter: q,
      });
    }

    const lastQ = quarterly[quarterly.length - 1];
    annual.push({
      revenue: annualRevenue,
      expenses: annualExpenses,
      assets: lastQ.assets,
      liabilities: lastQ.liabilities,
      netIncome: annualRevenue - annualExpenses,
      totalDebt: lastQ.totalDebt,
      year,
      quarter: 0,
    });
  }

  return { quarterly, annual };
}

// --- Mock Companies ---

function makeCompany(
  name: string,
  size: number,
  foundingYear: number
): Company {
  const { quarterly, annual } = generateFinancialReports(name, foundingYear, size);
  return { name, size, foundingYear, quarterlyFinances: quarterly, annualFinances: annual };
}

const companies: Company[] = [
  makeCompany("Nexus Technologies", 4, 1985),
  makeCompany("Alpine Dynamics", 3, 1992),
  makeCompany("Solaris Energy", 2, 1996),
  makeCompany("Crescent Pharma", 3, 1988),
  makeCompany("Ironclad Industries", 2, 1994),
  makeCompany("Vertex Media", 1, 1998),
];

// --- Mock Stocks ---

const symbols = ["NXTS", "ALPD", "SLRE", "CRPH", "IRCI", "VTXM"];
const startPrices = [45.0, 28.5, 12.0, 67.0, 8.5, 3.2];

export const mockStocks: Stock[] = companies.map((company, i) => {
  const monthsOfHistory = (2000 - company.foundingYear) * 12;
  const priceHistory = generatePriceHistory(startPrices[i], Math.max(monthsOfHistory, 12));
  const currentPrice = priceHistory[priceHistory.length - 1];
  const lastMonthPrice = priceHistory.length > 1 ? priceHistory[priceHistory.length - 2] : currentPrice;

  const sixMonthGrowth =
    priceHistory.length >= 6
      ? parseFloat((((currentPrice / priceHistory[priceHistory.length - 6]) - 1) * 100).toFixed(2))
      : null;

  const fiveYearGrowth =
    priceHistory.length >= 60
      ? parseFloat((((currentPrice / priceHistory[priceHistory.length - 60]) - 1) * 100).toFixed(2))
      : null;

  const hype = Math.floor(Math.random() * 200) - 100;

  return {
    symbol: symbols[i],
    price: currentPrice,
    lastMonthPrice,
    totalShares: (company.size + 1) * 100_000,
    hype,
    sixMonthGrowth,
    fiveYearGrowth,
    avgSixMonthGrowth: sixMonthGrowth ?? 0,
    avgFiveYearGrowth: fiveYearGrowth ?? 0,
    priceHistory,
    company,
  };
});

// --- Mock LLM Agent Portfolio ---

export const mockAgentPortfolio: Portfolio = {
  cash: 85_420.5,
  holdings: [
    { symbol: "NXTS", shares: 120, price: mockStocks[0].price },
    { symbol: "CRPH", shares: 45, price: mockStocks[3].price },
    { symbol: "SLRE", shares: 200, price: mockStocks[2].price },
  ],
};

// --- Mock Agent Activity Log ---

const actionTemplates = [
  { action: "BUY", reasoning: "Strong 6-month growth momentum and positive hype score. Company financials show improving revenue trend." },
  { action: "SELL", reasoning: "Hype declining rapidly and price dropped below 6-month average. Taking profits before further decline." },
  { action: "HOLD", reasoning: "Market conditions are mixed. Waiting for clearer signals before making any trades." },
  { action: "BUY", reasoning: "Undervalued based on company fundamentals. Revenue growth outpacing expenses, positive net income trend." },
  { action: "SELL", reasoning: "Position became overweight in portfolio. Rebalancing to reduce risk exposure." },
];

export const mockAgentActivities: AgentActivity[] = actionTemplates.map((t, i) => {
  const stock = mockStocks[i % mockStocks.length];
  const shares = Math.floor(Math.random() * 100) + 10;
  return {
    id: `act-${i}`,
    timestamp: new Date(Date.now() - (actionTemplates.length - i) * 30_000).toISOString(),
    reasoning: t.reasoning,
    action:
      t.action === "HOLD"
        ? "HOLD — no action taken"
        : `${t.action} ${shares} ${stock.symbol} @ $${stock.price.toFixed(2)}`,
  };
});

// --- Mock Clock ---

export const mockClock: MarketClock = {
  year: 2000,
  month: 3,
  day: 45,
  quarter: 1,
};
