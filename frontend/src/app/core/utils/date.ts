// src/app/core/utils/date.ts
// Local-time date helpers. Do NOT use new Date().toISOString() for "today":
// it returns UTC, so in India between 12:00am and 5:30am it gives
// YESTERDAY (and on the 1st of a month, LAST month).

const pad = (n: number): string => String(n).padStart(2, '0');

/** Today as YYYY-MM-DD in the user's local timezone. */
export function todayLocal(): string {
  const d = new Date();
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}

/** Current month as YYYY-MM (value format of <input type="month">). */
export function currentMonth(): string {
  const d = new Date();
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}`;
}

/** "2026-10-05" -> "2026-10" */
export function monthOf(date: string | null | undefined): string {
  return date ? date.slice(0, 7) : '';
}

/** "2026-10" -> "October 2026"; empty -> "All time" */
export function formatMonthLabel(yearMonth: string): string {
  if (!yearMonth) {
    return 'All time';
  }
  const [year, month] = yearMonth.split('-').map(Number);
  return new Date(year, month - 1, 1).toLocaleDateString('en-IN', {
    month: 'long',
    year: 'numeric'
  });
}