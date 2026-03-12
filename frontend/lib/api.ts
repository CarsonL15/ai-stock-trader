import { Stock, Portfolio, AgentActivity, MarketClock } from "./types";
import {
  mockStocks,
  mockAgentPortfolio,
  mockAgentActivities,
  mockClock,
} from "./mockData";

const JAVA_API_URL = process.env.NEXT_PUBLIC_JAVA_API_URL || "";

// Backend response shapes (what the Java API actually returns)

interface BackendStockListing {
  stockListingName: string;
  associatedCompanyName: string;
  totalSharesInExistence: number;
  lastSalePrice: number;
  hype: string;
  hypeNumeric: number;
  avgSixMonthGrowth: number;
  avgFiveYearGrowth: number;
  sixMonthGrowth: number;
  fiveYearGrowth: number;
  priceHistory: number[];
  numOfSellOrder: number;
  numOfBuyOrders: number;
}

interface BackendCompany {
  companyName: string;
  stockName: string;
  size: number;
  foundingYear: number;
  averageSixMonthCompanyGrowth: number;
  averageFiveYearCompanyGrowth: number;
  oneYearCompanyGrowth: number;
  fiveYearCompanyGrowth: number;
  annualFinances: BackendFinancialReport[];
}

interface BackendFinancialReport {
  assets: number;
  liabilities: number;
  revenue: number;
  expenses: number;
  netIncome: number;
  year: number;
  quarter: number;
  company: string;
}

// Transform helpers

function transformStock(
  listing: BackendStockListing,
  company: BackendCompany | undefined
): Stock {
  const history = listing.priceHistory ?? [];
  return {
    symbol: listing.stockListingName,
    price: listing.lastSalePrice,
    lastMonthPrice: history.length > 1 ? history[history.length - 2] : listing.lastSalePrice,
    totalShares: listing.totalSharesInExistence,
    hype: listing.hypeNumeric,
    sixMonthGrowth: listing.sixMonthGrowth,
    fiveYearGrowth: listing.fiveYearGrowth,
    avgSixMonthGrowth: listing.avgSixMonthGrowth,
    avgFiveYearGrowth: listing.avgFiveYearGrowth,
    priceHistory: history,
    company: {
      name: company?.companyName ?? listing.associatedCompanyName,
      size: company?.size ?? 0,
      foundingYear: company?.foundingYear ?? 2000,
      quarterlyFinances: [],
      annualFinances: (company?.annualFinances ?? []).map((r) => ({
        revenue: r.revenue,
        expenses: r.expenses,
        assets: r.assets,
        liabilities: r.liabilities,
        netIncome: r.netIncome,
        totalDebt: r.liabilities,
        year: r.year,
        quarter: r.quarter,
      })),
    },
  };
}

function parseDateString(dateStr: string): MarketClock {
  const yearMatch = dateStr.match(/Year:\s*(\d+)/);
  const monthMatch = dateStr.match(/Month:\s*(\d+)/);
  const dayMatch = dateStr.match(/Day:\s*(\d+)/);
  const year = yearMatch ? parseInt(yearMatch[1]) : 2000;
  const month = monthMatch ? parseInt(monthMatch[1]) : 1;
  const day = dayMatch ? parseInt(dayMatch[1]) : 1;
  let quarter = 1;
  if (day <= 90) quarter = 1;
  else if (day <= 181) quarter = 2;
  else if (day <= 273) quarter = 3;
  else quarter = 4;
  return { year, month, day, quarter };
}

// API functions

export async function getStocks(): Promise<Stock[]> {
  if (JAVA_API_URL) {
    const [listingsRes, companiesRes] = await Promise.all([
      fetch(`${JAVA_API_URL}/api/getAllStockListingInfo`),
      fetch(`${JAVA_API_URL}/api/getAllCompanyInfo`),
    ]);
    const listings: BackendStockListing[] = await listingsRes.json();
    const companies: BackendCompany[] = await companiesRes.json();

    const companyMap = new Map<string, BackendCompany>();
    for (const c of companies) {
      companyMap.set(c.companyName, c);
    }

    return listings.map((l) => transformStock(l, companyMap.get(l.associatedCompanyName)));
  }
  return mockStocks;
}

export async function getStockDetail(symbol: string): Promise<Stock | null> {
  if (JAVA_API_URL) {
    const stocks = await getStocks();
    return stocks.find((s) => s.symbol === symbol) ?? null;
  }
  return mockStocks.find((s) => s.symbol === symbol) ?? null;
}

export async function getLlmPortfolio(): Promise<Portfolio> {
  if (JAVA_API_URL) {
    const [cashRes, heldRes] = await Promise.all([
      fetch(`${JAVA_API_URL}/api/getCash`),
      fetch(`${JAVA_API_URL}/api/getUnlistedStock`),
    ]);
    const cash: number = await cashRes.json();
    const held: Record<string, { shares: number; name: string; price: number }> = await heldRes.json();

    const holdings = Object.values(held).map((s) => ({
      symbol: s.name,
      shares: s.shares,
      price: s.price,
    }));

    return { cash, holdings };
  }
  return mockAgentPortfolio;
}

export async function getClock(): Promise<MarketClock> {
  if (JAVA_API_URL) {
    const res = await fetch(`${JAVA_API_URL}/api/getDate`);
    const dateStr: string = await res.text();
    return parseDateString(dateStr);
  }
  return mockClock;
}

export async function evaluateAgent(): Promise<AgentActivity> {
  // In production, this calls our Next.js API route which calls Claude
  const res = await fetch("/api/agent/evaluate", { method: "POST" });
  return res.json();
}

export function getAgentActivityLog(): AgentActivity[] {
  return mockAgentActivities;
}
