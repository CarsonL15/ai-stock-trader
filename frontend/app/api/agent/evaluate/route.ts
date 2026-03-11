import { NextResponse } from "next/server";
import { AgentActivity } from "@/lib/types";

// Mock agent evaluation endpoint.
// When the Java backend + Claude API are integrated, this will:
// 1. Fetch market data from the Java backend
// 2. Call Claude with tool_use to analyze and trade
// 3. Return the agent's reasoning and action

const symbols = ["NXTS", "ALPD", "SLRE", "CRPH", "IRCI", "VTXM"];

const reasonings = [
  "NXTS showing strong upward momentum over the past 6 months with consistent revenue growth. Hype score is favorable. Entering a position.",
  "CRPH has declining hype and weakening quarterly financials. Reducing exposure to limit downside risk.",
  "Market conditions are uncertain. Multiple stocks showing mixed signals. Holding current positions and waiting for clearer trends.",
  "SLRE is undervalued relative to its fundamentals. Revenue growing faster than expenses, and the 5-year growth trajectory is strong. Accumulating shares.",
  "ALPD has reached my target price. Taking profits on half the position to lock in gains while maintaining some upside exposure.",
  "IRCI showing early signs of a turnaround. Quarterly revenue improved and hype is recovering from lows. Small speculative buy.",
  "VTXM is a small-cap with high volatility but the company financials are solid for its size. Adding a small position for diversification.",
  "Portfolio is overweight in tech stocks. Selling some NXTS to rebalance into IRCI for sector diversification.",
];

let evalCounter = 0;

export async function POST() {
  const idx = evalCounter % reasonings.length;
  evalCounter++;

  const reasoning = reasonings[idx];
  const symbol = symbols[Math.floor(Math.random() * symbols.length)];
  const shares = Math.floor(Math.random() * 80) + 10;
  const price = parseFloat((Math.random() * 60 + 3).toFixed(2));

  let action: string;
  if (reasoning.includes("Holding") || reasoning.includes("uncertain")) {
    action = "HOLD — no action taken";
  } else if (
    reasoning.includes("Reducing") ||
    reasoning.includes("Taking profits") ||
    reasoning.includes("Selling")
  ) {
    action = `SELL ${shares} ${symbol} @ $${price}`;
  } else {
    action = `BUY ${shares} ${symbol} @ $${price}`;
  }

  const activity: AgentActivity = {
    id: `eval-${Date.now()}`,
    timestamp: new Date().toISOString(),
    reasoning,
    action,
  };

  return NextResponse.json(activity);
}
