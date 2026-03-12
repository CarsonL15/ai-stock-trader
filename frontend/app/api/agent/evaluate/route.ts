import { NextResponse } from "next/server";
import { GoogleGenAI, Type, FunctionCallingConfigMode, Tool } from "@google/genai";
import { AgentActivity } from "@/lib/types";

const JAVA_API_URL = process.env.NEXT_PUBLIC_JAVA_API_URL || "";
const ai = new GoogleGenAI({ apiKey: process.env.GEMINI_API_KEY || "" });

// Tool definitions

const tools: Tool[] = [
  {
    functionDeclarations: [
      {
        name: "getMarketOverview",
        description:
          "Get a summary of all stocks on the market including their symbol, price, hype (-100 to 100), 6-month growth %, and company name. Use this to scan the market for opportunities.",
        parameters: {
          type: Type.OBJECT,
          properties: {
            unused: { type: Type.STRING, description: "Not used, pass any value" },
          },
        },
      },
      {
        name: "getStockDetail",
        description:
          "Get detailed info on a specific stock including full price history, company financials (revenue, expenses, net income, assets, liabilities), company size, and founding year. Use this to do deep analysis before buying or selling.",
        parameters: {
          type: Type.OBJECT,
          properties: {
            name: {
              type: Type.STRING,
              description: "The stock listing name (symbol)",
            },
          },
          required: ["name"],
        },
      },
      {
        name: "getPortfolio",
        description:
          "Get the current portfolio including cash balance and all stock holdings with share counts and prices.",
        parameters: {
          type: Type.OBJECT,
          properties: {
            unused: { type: Type.STRING, description: "Not used, pass any value" },
          },
        },
      },
      {
        name: "buyStock",
        description:
          "Place a market buy order for a stock. Automatically buys at the best available sell price. Only works if there are active sell orders for the stock. No need to specify a price.",
        parameters: {
          type: Type.OBJECT,
          properties: {
            name: {
              type: Type.STRING,
              description: "The stock listing name (symbol) to buy",
            },
            shares: {
              type: Type.NUMBER,
              description: "Number of shares to buy",
            },
          },
          required: ["name", "shares"],
        },
      },
      {
        name: "sellStock",
        description:
          "Place a market sell order for a stock you currently hold. Automatically sells at the best available buy price. Only works if there are active buy orders for the stock. No need to specify a price.",
        parameters: {
          type: Type.OBJECT,
          properties: {
            name: {
              type: Type.STRING,
              description: "The stock listing name (symbol) to sell",
            },
            shares: {
              type: Type.NUMBER,
              description: "Number of shares to sell",
            },
          },
          required: ["name", "shares"],
        },
      },
    ],
  },
];

// Tool implementations — call the Java backend directly (server-to-server, no CORS issues)

async function getMarketOverview(): Promise<string> {
  const [listingsRes, companiesRes] = await Promise.all([
    fetch(`${JAVA_API_URL}/api/getAllStockListingInfo`),
    fetch(`${JAVA_API_URL}/api/getAllCompanyInfo`),
  ]);
  const listings = await listingsRes.json();
  const companies = await companiesRes.json();

  const companyMap = new Map<string, any>();
  for (const c of companies) {
    companyMap.set(c.companyName, c);
  }

  // Return a condensed summary — sending all 2000 stocks would use too many tokens
  const summary = listings
    .map((l: any) => ({
      symbol: l.stockListingName,
      price: l.lastSalePrice,
      hype: l.hypeNumeric,
      fiveYearGrowth: l.fiveYearGrowth,
      sellOrders: l.numOfSellOrder,
      buyOrders: l.numOfBuyOrders,
      company: l.associatedCompanyName,
    }));

  // Only consider stocks that have active sell orders (these can actually be bought)
  const tradeable = summary.filter((s: any) => s.sellOrders > 0);

  // Sort by hype and growth to surface interesting stocks
  const topByHype = [...tradeable].sort((a: any, b: any) => b.hype - a.hype).slice(0, 20);
  const bottomByHype = [...tradeable].sort((a: any, b: any) => a.hype - b.hype).slice(0, 10);
  const topGrowth = [...tradeable]
    .sort((a: any, b: any) => (b.fiveYearGrowth ?? 0) - (a.fiveYearGrowth ?? 0))
    .slice(0, 20);
  const mostActive = [...summary]
    .sort((a: any, b: any) => (b.sellOrders + b.buyOrders) - (a.sellOrders + a.buyOrders))
    .slice(0, 20);

  return JSON.stringify({
    totalStocks: summary.length,
    tradeableStocks: tradeable.length,
    topHype: topByHype,
    lowestHype: bottomByHype,
    topGrowth: topGrowth,
    mostActive: mostActive,
  });
}

async function getStockDetail(name: string): Promise<string> {
  const [listingRes, companyRes] = await Promise.all([
    fetch(`${JAVA_API_URL}/api/getAllStockListingInfo`),
    fetch(`${JAVA_API_URL}/api/getAllCompanyInfo`),
  ]);
  const listings = await listingRes.json();
  const companies = await companyRes.json();

  const listing = listings.find(
    (l: any) => l.stockListingName === name
  );
  const company = companies.find(
    (c: any) => c.stockName === name || c.companyName === listing?.associatedCompanyName
  );

  if (!listing) return JSON.stringify({ error: `Stock ${name} not found` });

  return JSON.stringify({
    symbol: listing.stockListingName,
    price: listing.lastSalePrice,
    hype: listing.hypeNumeric,
    hypeDescription: listing.hype,
    totalShares: listing.totalSharesInExistence,
    sixMonthGrowth: listing.sixMonthGrowth,
    fiveYearGrowth: listing.fiveYearGrowth,
    avgSixMonthGrowth: listing.avgSixMonthGrowth,
    avgFiveYearGrowth: listing.avgFiveYearGrowth,
    recentPriceHistory: listing.priceHistory.slice(-12),
    numSellOrders: listing.numOfSellOrder,
    numBuyOrders: listing.numOfBuyOrders,
    company: company
      ? {
          name: company.companyName,
          size: company.size,
          foundingYear: company.foundingYear,
          annualFinances: company.annualFinances?.slice(-3),
        }
      : null,
  });
}

async function getPortfolio(): Promise<string> {
  const [cashRes, heldRes] = await Promise.all([
    fetch(`${JAVA_API_URL}/api/getCash`),
    fetch(`${JAVA_API_URL}/api/getUnlistedStock`),
  ]);
  const cash = await cashRes.json();
  const held = await heldRes.json();

  const holdings = Object.values(held).map((s: any) => ({
    symbol: s.name,
    shares: s.shares,
    price: s.price,
  }));

  return JSON.stringify({ cash, holdings, totalHoldings: holdings.length });
}

async function buyStock(
  name: string,
  shares: number
): Promise<string> {
  const res = await fetch(`${JAVA_API_URL}/api/placeMarketBuyOrder`, {
    method: "POST",
    body: JSON.stringify({ name, shares, price: 0 }),
    headers: { "Content-Type": "application/json" },
  });
  return await res.text();
}

async function sellStock(
  name: string,
  shares: number
): Promise<string> {
  const res = await fetch(`${JAVA_API_URL}/api/placeMarketSellOrder`, {
    method: "POST",
    body: JSON.stringify({ name, shares, price: 0 }),
    headers: { "Content-Type": "application/json" },
  });
  return await res.text();
}

// Tool dispatcher
const toolFunctions: Record<string, (args: any) => Promise<string>> = {
  getMarketOverview: () => getMarketOverview(),
  getStockDetail: (args) => getStockDetail(args.name),
  getPortfolio: () => getPortfolio(),
  buyStock: (args) => buyStock(args.name, args.shares),
  sellStock: (args) => sellStock(args.name, args.shares),
};

const SYSTEM_PROMPT = `You are an AI stock trading agent managing a portfolio in a simulated stock market.

Your goal is to grow your portfolio value over time by making smart trades. You are competing against 15,000 simple algorithmic bots (passive, medium, and aggressive strategies).

How orders work:
- Buy and sell orders are MARKET ORDERS — they execute at the best available price automatically.
- A buy order fills at the cheapest available sell price. It requires active sell orders (sellOrders > 0).
- A sell order fills at the highest available buy price. It requires active buy orders (buyOrders > 0).
- You do NOT specify a price — just the stock name and number of shares.
- If there are no counterparty orders, the trade will fail. Always check sellOrders/buyOrders counts.

Strategy guidelines:
- Start each evaluation by checking your portfolio, then scan the market overview
- ONLY buy stocks that have active sell orders (sellOrders > 0)
- ONLY sell stocks that have active buy orders (buyOrders > 0)
- Prefer stocks from the "mostActive" list — these have the most liquidity
- Look for stocks with high positive hype AND strong 5-year growth — these have momentum
- Diversify across multiple stocks, don't put everything in one
- Keep some cash reserve (at least 10-20%) for opportunities
- Use getStockDetail to dig into financials before making large trades
- Don't overtrade — only act when you see a clear opportunity
- Consider the company's size, founding year, revenue trends, and debt levels

When you're done analyzing and trading, respond with a brief summary of what you did and why.`;

export async function POST() {
  try {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    let contents: any[] = [
      {
        role: "user",
        parts: [
          {
            text: "Evaluate the current market conditions and manage the portfolio. Check your holdings, scan the market, investigate any interesting opportunities, and make trades if appropriate.",
          },
        ],
      },
    ];

    const config = {
      tools,
      systemInstruction: SYSTEM_PROMPT,
      toolConfig: {
        functionCallingConfig: {
          mode: FunctionCallingConfigMode.AUTO,
        },
      },
    };

    let finalText = "";
    let actions: string[] = [];
    let iterations = 0;
    const maxIterations = 15;

    while (iterations < maxIterations) {
      iterations++;

      const result = await ai.models.generateContent({
        model: "gemini-2.5-flash",
        contents,
        config,
      });

      if (result.functionCalls && result.functionCalls.length > 0) {
        // Process each function call
        for (const functionCall of result.functionCalls) {
          const fnName = functionCall.name ?? "";
          const args = functionCall.args ?? {};

          if (!toolFunctions[fnName]) {
            throw new Error(`Unknown function: ${fnName}`);
          }

          const toolResponse = await toolFunctions[fnName](args);

          // Track buy/sell actions for the activity log (include backend response)
          if (fnName === "buyStock") {
            actions.push(
              `BUY ${args.shares} ${args.name} → ${toolResponse}`
            );
          } else if (fnName === "sellStock") {
            actions.push(
              `SELL ${args.shares} ${args.name} → ${toolResponse}`
            );
          }

          // Add model's function call to conversation
          contents.push({
            role: "model",
            parts: [{ functionCall }],
          });

          // Add function result
          contents.push({
            role: "user",
            parts: [
              {
                functionResponse: {
                  name: functionCall.name,
                  response: { result: toolResponse },
                },
              },
            ],
          });
        }
      } else {
        // Model is done — final text response
        finalText = result.text || "Evaluation complete.";
        break;
      }
    }

    const action =
      actions.length > 0 ? actions.join(", ") : "HOLD — no action taken";

    // Fetch current portfolio value for the chart
    let portfolioValue = 0;
    try {
      const [cashRes, heldRes] = await Promise.all([
        fetch(`${JAVA_API_URL}/api/getCash`),
        fetch(`${JAVA_API_URL}/api/getUnlistedStock`),
      ]);
      const cash = await cashRes.json();
      const held = await heldRes.json();
      const holdingsValue = Object.values(held).reduce(
        (sum: number, s: any) => sum + s.shares * s.price,
        0
      );
      portfolioValue = cash + holdingsValue;
    } catch {
      // ignore — value stays 0
    }

    const activity: AgentActivity = {
      id: `eval-${Date.now()}`,
      timestamp: new Date().toISOString(),
      reasoning: finalText,
      action,
      portfolioValue,
    };

    return NextResponse.json(activity);
  } catch (error) {
    console.error("Agent evaluation error:", error);

    const activity: AgentActivity = {
      id: `eval-${Date.now()}`,
      timestamp: new Date().toISOString(),
      reasoning: `Error: ${error instanceof Error ? error.message : "Unknown error"}`,
      action: "HOLD — error occurred",
    };

    return NextResponse.json(activity);
  }
}
