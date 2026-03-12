"use client";

import { useState } from "react";
import { Stock } from "@/lib/types";

type SortKey = "symbol" | "price" | "hype" | "growth";

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
  const [search, setSearch] = useState("");

  function handleSort(key: SortKey) {
    if (sortKey === key) {
      setSortAsc(!sortAsc);
    } else {
      setSortKey(key);
      setSortAsc(key === "symbol");
    }
  }

  const filtered = stocks.filter((s) => {
    if (!search) return true;
    const q = search.toLowerCase();
    return (
      s.symbol.toLowerCase().includes(q) ||
      s.company.name.toLowerCase().includes(q)
    );
  });

  const sorted = [...filtered].sort((a, b) => {
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
      case "growth":
        cmp = (a.fiveYearGrowth ?? -Infinity) - (b.fiveYearGrowth ?? -Infinity);
        break;
      default:
        cmp = 0;
    }
    return sortAsc ? cmp : -cmp;
  });

  const headerClass =
    "px-3 py-2 text-left text-xs font-medium text-gray-400 uppercase tracking-wider cursor-pointer hover:text-gray-200 select-none";
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
    <div className="flex flex-col h-full">
      {/* Search */}
      <div className="p-3 border-b border-gray-800">
        <input
          type="text"
          placeholder="Search stocks or companies..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="w-full bg-gray-800 text-white text-sm px-3 py-2 rounded border border-gray-700 focus:border-blue-500 focus:outline-none placeholder-gray-500"
        />
        <div className="text-xs text-gray-500 mt-1.5">
          {filtered.length} stocks{search && ` matching "${search}"`}
        </div>
      </div>

      {/* Table header */}
      <div className="shrink-0">
        <table className="w-full table-fixed">
          <colgroup>
            <col className="w-[35%]" />
            <col className="w-[25%]" />
            <col className="w-[20%]" />
            <col className="w-[20%]" />
          </colgroup>
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
              <th className={headerClass} onClick={() => handleSort("growth")}>
                5Y{arrow("growth")}
              </th>
            </tr>
          </thead>
        </table>
      </div>

      {/* Scrollable body */}
      <div className="flex-1 overflow-y-auto">
        <table className="w-full table-fixed">
          <colgroup>
            <col className="w-[35%]" />
            <col className="w-[25%]" />
            <col className="w-[20%]" />
            <col className="w-[20%]" />
          </colgroup>
          <tbody>
            {sorted.map((stock) => (
              <tr
                key={stock.symbol}
                onClick={() => onSelect(stock.symbol)}
                className={`border-b border-gray-800/50 cursor-pointer transition-colors ${
                  selectedSymbol === stock.symbol
                    ? "bg-blue-900/30 border-l-2 border-l-blue-500"
                    : "hover:bg-gray-800/50"
                }`}
              >
                <td className="px-3 py-2">
                  <div className="font-mono font-bold text-white text-sm truncate">
                    {stock.symbol}
                  </div>
                  <div className="text-xs text-gray-500 truncate">
                    {stock.company.name}
                  </div>
                </td>
                <td className="px-3 py-2 font-mono text-sm truncate">
                  ${stock.price.toFixed(2)}
                </td>
                <td className={`px-3 py-2 font-mono text-sm ${hypeColor(stock.hype)}`}>
                  {stock.hype > 0 ? "+" : ""}
                  {stock.hype}
                </td>
                <td className={`px-3 py-2 font-mono text-sm ${growthColor(stock.fiveYearGrowth)}`}>
                  {stock.fiveYearGrowth !== null
                    ? `${stock.fiveYearGrowth > 0 ? "+" : ""}${stock.fiveYearGrowth.toFixed(1)}%`
                    : "—"}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
