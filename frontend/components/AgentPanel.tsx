"use client";

import { Portfolio, AgentActivity } from "@/lib/types";

export default function AgentPanel({
  portfolio,
  activities,
}: {
  portfolio: Portfolio;
  activities: AgentActivity[];
}) {
  const totalHoldingsValue = portfolio.holdings.reduce(
    (sum, h) => sum + h.shares * h.price,
    0
  );

  return (
    <div className="bg-gray-900 rounded-lg border border-gray-800">
      {/* Header */}
      <div className="px-4 py-3 border-b border-gray-800 flex items-center justify-between">
        <h2 className="text-sm font-bold text-white uppercase tracking-wider">
          LLM Trading Agent
        </h2>
        <span className="text-xs bg-blue-900/50 text-blue-400 px-2 py-0.5 rounded">
          Auto-Trading
        </span>
      </div>

      {/* Portfolio Summary */}
      <div className="px-4 py-3 border-b border-gray-800 grid grid-cols-3 gap-4">
        <div>
          <div className="text-xs text-gray-400">Cash</div>
          <div className="text-lg font-mono text-white">
            ${portfolio.cash.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
          </div>
        </div>
        <div>
          <div className="text-xs text-gray-400">Holdings Value</div>
          <div className="text-lg font-mono text-white">
            ${totalHoldingsValue.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
          </div>
        </div>
        <div>
          <div className="text-xs text-gray-400">Total Value</div>
          <div className="text-lg font-mono text-green-400">
            ${(portfolio.cash + totalHoldingsValue).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
          </div>
        </div>
      </div>

      {/* Holdings */}
      {portfolio.holdings.length > 0 && (
        <div className="px-4 py-3 border-b border-gray-800">
          <div className="text-xs text-gray-400 uppercase mb-2">Holdings</div>
          <div className="space-y-1">
            {portfolio.holdings.map((h) => (
              <div
                key={h.symbol}
                className="flex justify-between text-sm font-mono"
              >
                <span className="text-white">
                  {h.shares} {h.symbol}
                </span>
                <span className="text-gray-400">
                  ${(h.shares * h.price).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Activity Log */}
      <div className="px-4 py-3">
        <div className="text-xs text-gray-400 uppercase mb-2">Activity Log</div>
        <div className="space-y-2 max-h-64 overflow-y-auto">
          {activities.length === 0 ? (
            <p className="text-sm text-gray-500 italic">
              Waiting for first evaluation...
            </p>
          ) : (
            [...activities].reverse().map((a) => (
              <div
                key={a.id}
                className="text-sm border-l-2 border-gray-700 pl-3 py-1"
              >
                <div className="flex items-center gap-2">
                  <span
                    className={`font-mono text-xs font-bold ${
                      a.action.startsWith("BUY")
                        ? "text-green-400"
                        : a.action.startsWith("SELL")
                        ? "text-red-400"
                        : "text-gray-400"
                    }`}
                  >
                    {a.action}
                  </span>
                  <span className="text-xs text-gray-600">
                    {new Date(a.timestamp).toLocaleTimeString()}
                  </span>
                </div>
                <p className="text-gray-400 text-xs mt-0.5">{a.reasoning}</p>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
