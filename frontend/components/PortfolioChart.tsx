"use client";

import { AgentActivity } from "@/lib/types";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
  ReferenceLine,
} from "recharts";

export default function PortfolioChart({
  activities,
}: {
  activities: AgentActivity[];
}) {
  const data = activities
    .filter((a) => a.portfolioValue && a.portfolioValue > 0)
    .map((a, i) => ({
      index: i,
      value: a.portfolioValue!,
      time: new Date(a.timestamp).toLocaleTimeString(),
    }));

  if (data.length < 2) {
    return (
      <div className="bg-gray-900 rounded-lg border border-gray-800 p-4">
        <h3 className="text-sm font-bold text-gray-400 uppercase tracking-wider mb-3">
          Portfolio Value
        </h3>
        <div className="flex items-center justify-center h-[150px] text-gray-500 text-sm">
          Waiting for agent evaluations...
        </div>
      </div>
    );
  }

  const startingValue = 1_000_000;
  const currentValue = data[data.length - 1].value;
  const change = currentValue - startingValue;
  const changePercent = ((change / startingValue) * 100).toFixed(2);
  const isPositive = change >= 0;

  const minVal = Math.min(...data.map((d) => d.value)) * 0.995;
  const maxVal = Math.max(...data.map((d) => d.value)) * 1.005;

  return (
    <div className="bg-gray-900 rounded-lg border border-gray-800 p-4">
      <div className="flex items-center justify-between mb-3">
        <h3 className="text-sm font-bold text-gray-400 uppercase tracking-wider">
          Portfolio Value
        </h3>
        <div className="text-right">
          <span className="text-lg font-mono text-white">
            ${currentValue.toLocaleString(undefined, {
              minimumFractionDigits: 2,
              maximumFractionDigits: 2,
            })}
          </span>
          <span
            className={`ml-2 text-sm font-mono ${
              isPositive ? "text-green-400" : "text-red-400"
            }`}
          >
            {isPositive ? "+" : ""}
            {changePercent}%
          </span>
        </div>
      </div>

      <ResponsiveContainer width="100%" height={150}>
        <LineChart data={data}>
          <CartesianGrid strokeDasharray="3 3" stroke="#1f2937" />
          <XAxis
            dataKey="time"
            stroke="#6b7280"
            tick={{ fontSize: 10 }}
            interval="preserveStartEnd"
          />
          <YAxis
            domain={[minVal, maxVal]}
            stroke="#6b7280"
            tick={{ fontSize: 10 }}
            tickFormatter={(v) => `$${(v / 1000).toFixed(0)}k`}
          />
          <Tooltip
            contentStyle={{
              backgroundColor: "#111827",
              border: "1px solid #374151",
              borderRadius: "8px",
              fontSize: "12px",
            }}
            formatter={(value: number | undefined) =>
              value != null
                ? [
                    `$${value.toLocaleString(undefined, {
                      minimumFractionDigits: 2,
                      maximumFractionDigits: 2,
                    })}`,
                    "Value",
                  ]
                : ["—", "Value"]
            }
          />
          <ReferenceLine
            y={startingValue}
            stroke="#6b7280"
            strokeDasharray="3 3"
          />
          <Line
            type="monotone"
            dataKey="value"
            stroke={isPositive ? "#22c55e" : "#ef4444"}
            strokeWidth={2}
            dot={false}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
