"use client";

import { useState, useEffect, useCallback } from "react";
import { Stock, Portfolio, AgentActivity, MarketClock as ClockType } from "@/lib/types";
import { getStocks, getLlmPortfolio, getClock, evaluateAgent, getAgentActivityLog } from "@/lib/api";
import MarketClock from "@/components/MarketClock";
import StockTable from "@/components/StockTable";
import PriceChart from "@/components/PriceChart";
import AgentPanel from "@/components/AgentPanel";

export default function Dashboard() {
  const [stocks, setStocks] = useState<Stock[]>([]);
  const [clock, setClock] = useState<ClockType | null>(null);
  const [portfolio, setPortfolio] = useState<Portfolio | null>(null);
  const [activities, setActivities] = useState<AgentActivity[]>([]);
  const [selectedSymbol, setSelectedSymbol] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  const fetchData = useCallback(async () => {
    try {
      const [stockData, clockData, portfolioData] = await Promise.all([
        getStocks(),
        getClock(),
        getLlmPortfolio(),
      ]);
      setStocks(stockData);
      setClock(clockData);
      setPortfolio(portfolioData);
    } catch (err) {
      console.error("Failed to fetch market data:", err);
    }
  }, []);

  // Initial load
  useEffect(() => {
    async function init() {
      setActivities(getAgentActivityLog());
      await fetchData();
      setLoading(false);
    }
    init();
  }, [fetchData]);

  // Refresh market data every 5 seconds
  useEffect(() => {
    const interval = setInterval(fetchData, 5000);
    return () => clearInterval(interval);
  }, [fetchData]);

  // Agent auto-evaluates every 30 seconds
  useEffect(() => {
    const interval = setInterval(async () => {
      try {
        const activity = await evaluateAgent();
        setActivities((prev) => [...prev, activity]);
        const portfolioData = await getLlmPortfolio();
        setPortfolio(portfolioData);
      } catch (err) {
        console.error("Agent evaluation failed:", err);
      }
    }, 30000);
    return () => clearInterval(interval);
  }, []);

  const selectedStock = stocks.find((s) => s.symbol === selectedSymbol) ?? null;

  if (loading) {
    return (
      <div className="min-h-screen bg-black flex items-center justify-center">
        <div className="text-gray-400 text-lg">Loading market data...</div>
      </div>
    );
  }

  return (
    <div className="h-screen bg-black text-white flex flex-col overflow-hidden">
      {/* Header */}
      <header className="border-b border-gray-800 px-6 py-3 flex items-center justify-between shrink-0">
        <h1 className="text-xl font-bold tracking-tight">AI Stock Trader</h1>
        {clock && <MarketClock clock={clock} />}
      </header>

      {/* Main content - fills remaining height */}
      <div className="flex-1 flex overflow-hidden">
        {/* Left: Stock list */}
        <div className="w-[420px] shrink-0 border-r border-gray-800 flex flex-col">
          <StockTable
            stocks={stocks}
            selectedSymbol={selectedSymbol}
            onSelect={setSelectedSymbol}
          />
        </div>

        {/* Right: Chart + Agent */}
        <div className="flex-1 flex flex-col overflow-y-auto p-4 gap-4">
          {/* Price Chart */}
          {selectedStock ? (
            <PriceChart stock={selectedStock} />
          ) : (
            <div className="bg-gray-900 rounded-lg border border-gray-800 p-8 flex items-center justify-center min-h-[350px]">
              <p className="text-gray-500">
                Select a stock to view its price chart
              </p>
            </div>
          )}

          {/* Agent Panel */}
          {portfolio && (
            <AgentPanel portfolio={portfolio} activities={activities} />
          )}
        </div>
      </div>
    </div>
  );
}
