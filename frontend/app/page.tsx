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
        // Refresh portfolio after agent trades
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
    <div className="min-h-screen bg-black text-white">
      {/* Header */}
      <header className="border-b border-gray-800 px-6 py-4 flex items-center justify-between">
        <h1 className="text-xl font-bold tracking-tight">AI Stock Trader</h1>
        {clock && <MarketClock clock={clock} />}
      </header>

      <div className="max-w-7xl mx-auto px-6 py-6 space-y-6">
        {/* Market Overview */}
        <section>
          <h2 className="text-sm font-bold text-gray-400 uppercase tracking-wider mb-3">
            Market Overview
          </h2>
          <div className="bg-gray-900 rounded-lg border border-gray-800">
            <StockTable
              stocks={stocks}
              selectedSymbol={selectedSymbol}
              onSelect={setSelectedSymbol}
            />
          </div>
        </section>

        {/* Chart + Agent Panel side by side */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Price Chart */}
          <section>
            {selectedStock ? (
              <PriceChart stock={selectedStock} />
            ) : (
              <div className="bg-gray-900 rounded-lg border border-gray-800 p-8 flex items-center justify-center h-full min-h-[300px]">
                <p className="text-gray-500">
                  Select a stock to view its price chart
                </p>
              </div>
            )}
          </section>

          {/* Agent Panel */}
          <section>
            {portfolio && (
              <AgentPanel portfolio={portfolio} activities={activities} />
            )}
          </section>
        </div>
      </div>
    </div>
  );
}
