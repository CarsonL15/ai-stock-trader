import { Stock, Portfolio, AgentActivity, MarketClock } from "./types";
import {
  mockStocks,
  mockAgentPortfolio,
  mockAgentActivities,
  mockClock,
} from "./mockData";

// When the Java backend REST API is ready, replace these mock implementations
// with fetch() calls to JAVA_API_URL. The return types stay the same.

const JAVA_API_URL = process.env.NEXT_PUBLIC_JAVA_API_URL || "";

export async function getStocks(): Promise<Stock[]> {
  if (JAVA_API_URL) {
    const res = await fetch(`${JAVA_API_URL}/api/stocks`);
    return res.json();
  }
  return mockStocks;
}

export async function getStockDetail(symbol: string): Promise<Stock | null> {
  if (JAVA_API_URL) {
    const res = await fetch(`${JAVA_API_URL}/api/stocks/${symbol}`);
    return res.json();
  }
  return mockStocks.find((s) => s.symbol === symbol) ?? null;
}

export async function getLlmPortfolio(): Promise<Portfolio> {
  if (JAVA_API_URL) {
    const res = await fetch(`${JAVA_API_URL}/api/llm/portfolio`);
    return res.json();
  }
  return mockAgentPortfolio;
}

export async function getClock(): Promise<MarketClock> {
  if (JAVA_API_URL) {
    const res = await fetch(`${JAVA_API_URL}/api/clock`);
    return res.json();
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
