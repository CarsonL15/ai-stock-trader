"use client";

import { MarketClock as ClockType } from "@/lib/types";

const monthNames = [
  "", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
  "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
];

export default function MarketClock({ clock }: { clock: ClockType }) {
  return (
    <div className="flex items-center gap-3 text-sm text-gray-400">
      <span className="inline-block w-2 h-2 rounded-full bg-green-500 animate-pulse" />
      <span>
        Sim Date: {monthNames[clock.month]} {clock.day}, {clock.year}
      </span>
      <span className="text-gray-600">|</span>
      <span>Q{clock.quarter}</span>
    </div>
  );
}
