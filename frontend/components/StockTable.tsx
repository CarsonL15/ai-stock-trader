"use client";

import { useState } from "react";
import { Stock } from "@/lib/types";

type SortKey = "symbol" | "price" | "hype" | "sixMonthGrowth";

export default function StockTable({
  stocks,
  selectedSymbol,
  onSelect,
}: {
  stocks: Stock[];
  selectedSymbol: string | null;
  onSelect: (symbol: string) => void;
}) {
  const [sortKey, setSortKey] = useState<SortKey>("symbol");
  const [sortAsc, setSortAsc] = useState(true);

  function handleSort(key: SortKey) {
    if (sortKey === key) {
      setSortAsc(!sortAsc);
    } else {
      setSortKey(key);
      setSortAsc(key === "symbol");
    }
  }

  const sorted = [...stocks].sort((a, b) => {
    let cmp: number;
    switch (sortKey) {
      case "symbol":
        cmp = a.symbol.localeCompare(b.symbol);
        break;
      case "price":
        cmp = a.price - b.price;
        break;
      case "hype":
        cmp = a.hype - b.hype;
        break;
      case "sixMonthGrowth":
        cmp = (a.sixMonthGrowth ?? -Infinity) - (b.sixMonthGrowth ?? -Infinity);
        break;
      default:
        cmp = 0;
    }
    return sortAsc ? cmp : -cmp;
  });

  const headerClass = "px-4 py-2 text-left text-xs font-medium text-gray-400 uppercase tracking-wider cursor-pointer hover:text-gray-200 select-none";
  const arrow = (key: SortKey) =>
    sortKey === key ? (sortAsc ? " ↑" : " ↓") : "";

  function hypeColor(hype: number): string {
    if (hype >= 50) return "text-green-400";
    if (hype >= 0) return "text-green-600";
    if (hype >= -50) return "text-red-400";
    return "text-red-500";
  }

  function growthColor(val: number | null): string {
    if (val === null) return "text-gray-500";
    return val >= 0 ? "text-green-400" : "text-red-400";
  }

  return (
    <div className="overflow-x-auto">
      <table className="w-full">
        <thead>
          <tr className="border-b border-gray-800">
            <th className={headerClass} onClick={() => handleSort("symbol")}>
              Symbol{arrow("symbol")}
            </th>
            <th className={headerClass} onClick={() => handleSort("price")}>
              Price{arrow("price")}
            </th>
            <th className={headerClass} onClick={() => handleSort("hype")}>
              Hype{arrow("hype")}
            </th>
            <th className={headerClass} onClick={() => handleSort("sixMonthGrowth")}>
              6M Growth{arrow("sixMonthGrowth")}
            </th>
            <th className="px-4 py-2 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">
              Market Cap
            </th>
            <th className="px-4 py-2 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">
              Company
            </th>
          </tr>
        </thead>
        <tbody>
          {sorted.map((stock) => (
            <tr
              key={stock.symbol}
              onClick={() => onSelect(stock.symbol)}
              className={`border-b border-gray-800/50 cursor-pointer transition-colors ${
                selectedSymbol === stock.symbol
                  ? "bg-gray-800"
                  : "hover:bg-gray-900"
              }`}
            >
              <td className="px-4 py-3 font-mono font-bold text-white">
                {stock.symbol}
              </td>
              <td className="px-4 py-3 font-mono">
                ${stock.price.toFixed(2)}
              </td>
              <td className={`px-4 py-3 font-mono ${hypeColor(stock.hype)}`}>
                {stock.hype > 0 ? "+" : ""}
                {stock.hype}
              </td>
              <td className={`px-4 py-3 font-mono ${growthColor(stock.sixMonthGrowth)}`}>
                {stock.sixMonthGrowth !== null
                  ? `${stock.sixMonthGrowth > 0 ? "+" : ""}${stock.sixMonthGrowth.toFixed(1)}%`
                  : "—"}
              </td>
              <td className="px-4 py-3 font-mono text-gray-300">
                ${((stock.price * stock.totalShares) / 1_000_000).toFixed(1)}M
              </td>
              <td className="px-4 py-3 text-gray-400 text-sm">
                {stock.company.name}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
