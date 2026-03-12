"use client";

import { MarketClock as ClockType } from "@/lib/types";

const monthNames = [
  "", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
  "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
];

const monthDays = [0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

function getDayOfMonth(dayOfYear: number, month: number): number {
  let daysPassed = 0;
  for (let i = 1; i < month; i++) {
    daysPassed += monthDays[i];
  }
  return dayOfYear - daysPassed;
}

export default function MarketClock({ clock }: { clock: ClockType }) {
  const dayOfMonth = getDayOfMonth(clock.day, clock.month);

  return (
    <div className="flex items-center gap-3 text-sm text-gray-400">
      <span className="inline-block w-2 h-2 rounded-full bg-green-500 animate-pulse" />
      <span>
        Sim Date: {monthNames[clock.month]} {dayOfMonth}, {clock.year}
      </span>
      <span className="text-gray-600">|</span>
      <span>Q{clock.quarter}</span>
    </div>
  );
}
