"use client";

import { Stock } from "@/lib/types";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
} from "recharts";

export default function PriceChart({ stock }: { stock: Stock }) {
  const data = stock.priceHistory.map((price, i) => ({
    month: i,
    price: parseFloat(price.toFixed(2)),
  }));

  const minPrice = Math.min(...stock.priceHistory) * 0.9;
  const maxPrice = Math.max(...stock.priceHistory) * 1.1;

  return (
    <div className="bg-gray-900 rounded-lg p-4 border border-gray-800">
      <div className="flex items-center justify-between mb-4">
        <div>
          <h3 className="text-lg font-bold text-white">
            {stock.symbol}{" "}
            <span className="text-gray-400 font-normal text-sm">
              {stock.company.name}
            </span>
          </h3>
          <p className="text-2xl font-mono text-white">
            ${stock.price.toFixed(2)}
          </p>
        </div>
        <div className="text-right text-sm">
          <div className="text-gray-400">
            Hype:{" "}
            <span
              className={
                stock.hype >= 0 ? "text-green-400" : "text-red-400"
              }
            >
              {stock.hype > 0 ? "+" : ""}
              {stock.hype}
            </span>
          </div>
          <div className="text-gray-400">
            Shares: {stock.totalShares.toLocaleString()}
          </div>
          <div className="text-gray-400">
            Size: {["", "Small", "Medium", "Large", "Mega"][stock.company.size]}
          </div>
          <div className="text-gray-400">
            Founded: {stock.company.foundingYear}
          </div>
        </div>
      </div>

      <ResponsiveContainer width="100%" height={250}>
        <LineChart data={data}>
          <CartesianGrid strokeDasharray="3 3" stroke="#1f2937" />
          <XAxis
            dataKey="month"
            stroke="#6b7280"
            tick={{ fontSize: 11 }}
            tickFormatter={(v) => {
              const year = stock.company.foundingYear + Math.floor(v / 12);
              return v % 24 === 0 ? String(year) : "";
            }}
          />
          <YAxis
            domain={[minPrice, maxPrice]}
            stroke="#6b7280"
            tick={{ fontSize: 11 }}
            tickFormatter={(v) => `$${v.toFixed(0)}`}
          />
          <Tooltip
            contentStyle={{
              backgroundColor: "#111827",
              border: "1px solid #374151",
              borderRadius: "8px",
              fontSize: "12px",
            }}
            formatter={(value: number | undefined) => value != null ? [`$${value.toFixed(2)}`, "Price"] : ["—", "Price"]}
            labelFormatter={(label) => {
              const year =
                stock.company.foundingYear + Math.floor(label / 12);
              const month = (label % 12) + 1;
              return `${year}-${String(month).padStart(2, "0")}`;
            }}
          />
          <Line
            type="monotone"
            dataKey="price"
            stroke="#3b82f6"
            strokeWidth={1.5}
            dot={false}
          />
        </LineChart>
      </ResponsiveContainer>

      {/* Company financials summary */}
      {stock.company.annualFinances.length > 0 && (
        <div className="mt-4 pt-4 border-t border-gray-800">
          <h4 className="text-xs font-medium text-gray-400 uppercase mb-2">
            Latest Annual Financials (
            {stock.company.annualFinances[stock.company.annualFinances.length - 1].year})
          </h4>
          <div className="grid grid-cols-2 gap-2 text-sm">
            {(() => {
              const latest =
                stock.company.annualFinances[stock.company.annualFinances.length - 1];
              return (
                <>
                  <div className="text-gray-400">
                    Revenue:{" "}
                    <span className="text-white font-mono">
                      ${(latest.revenue / 1_000_000).toFixed(1)}M
                    </span>
                  </div>
                  <div className="text-gray-400">
                    Expenses:{" "}
                    <span className="text-white font-mono">
                      ${(latest.expenses / 1_000_000).toFixed(1)}M
                    </span>
                  </div>
                  <div className="text-gray-400">
                    Net Income:{" "}
                    <span
                      className={`font-mono ${
                        latest.netIncome >= 0
                          ? "text-green-400"
                          : "text-red-400"
                      }`}
                    >
                      ${(latest.netIncome / 1_000_000).toFixed(1)}M
                    </span>
                  </div>
                  <div className="text-gray-400">
                    Debt:{" "}
                    <span className="text-white font-mono">
                      ${(latest.totalDebt / 1_000_000).toFixed(1)}M
                    </span>
                  </div>
                </>
              );
            })()}
          </div>
        </div>
      )}
    </div>
  );
}
