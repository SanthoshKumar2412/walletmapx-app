import { Component, computed, input, model } from '@angular/core';

const MONTH_NAMES = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December'
];

const pad = (n: number): string => String(n).padStart(2, '0');

@Component({
  selector: 'app-month-picker',
  standalone: true,
  template: `
    <div class="flex items-center gap-1.5">

      @if (value()) {
        <button
          type="button"
          (click)="step(-1)"
          aria-label="Previous month"
          class="rounded-lg border border-gray-300 bg-white px-3 py-2
                 text-sm text-gray-600 hover:bg-gray-50"
        >‹</button>
      }

      <select
        aria-label="Month"
        (change)="onMonth(+$any($event.target).value)"
        class="rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm
               outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
      >
        @if (allowClear()) {
          <option value="0" [selected]="selectedMonth() === 0">All months</option>
        }

        @for (name of months; track name; let i = $index) {
          <option
            [value]="i + 1"
            [selected]="i + 1 === selectedMonth()"
            [disabled]="isMonthDisabled(i + 1)"
          >{{ name }}</option>
        }
      </select>

      <select
        aria-label="Year"
        [disabled]="!value()"
        (change)="onYear(+$any($event.target).value)"
        class="rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm
               outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100
               disabled:bg-gray-50 disabled:text-gray-400"
      >
        @for (y of years(); track y) {
          <option [value]="y" [selected]="y === selectedYear()">{{ y }}</option>
        }
      </select>

      @if (value()) {
        <button
          type="button"
          (click)="step(1)"
          [disabled]="!canNext()"
          aria-label="Next month"
          class="rounded-lg border border-gray-300 bg-white px-3 py-2
                 text-sm text-gray-600 hover:bg-gray-50
                 disabled:cursor-not-allowed disabled:opacity-40"
        >›</button>
      }

    </div>
  `
})
export class MonthPicker {

  /** 'YYYY-MM', or '' for "all months" (only when allowClear is true) */
  readonly value = model<string>('');

  /** Latest selectable month, 'YYYY-MM' (optional) */
  readonly max = input<string>('');

  readonly allowClear = input<boolean>(false);

  readonly months = MONTH_NAMES;

  private readonly nowYear = new Date().getFullYear();

  private readonly maxYear = computed(() =>
    this.max() ? Number(this.max().slice(0, 4)) : this.nowYear + 1
  );

  readonly selectedYear = computed(() =>
    this.value() ? Number(this.value().slice(0, 4)) : this.nowYear
  );

  readonly selectedMonth = computed(() =>
    this.value() ? Number(this.value().slice(5, 7)) : 0
  );

  readonly years = computed(() => {
    const list: number[] = [];
    for (let y = this.maxYear(); y >= this.maxYear() - 15; y--) {
      list.push(y);
    }
    const current = this.selectedYear();
    if (!list.includes(current)) {
      list.push(current);
      list.sort((a, b) => b - a);
    }
    return list;
  });

  readonly canNext = computed(() =>
    !this.max() || this.shift(1) <= this.max()
  );

  isMonthDisabled(month: number): boolean {
    const max = this.max();
    return !!max
      && this.selectedYear() === Number(max.slice(0, 4))
      && month > Number(max.slice(5, 7));
  }

  onMonth(month: number): void {
    if (month === 0) {
      this.value.set('');
      return;
    }
    this.value.set(this.clamp(`${this.selectedYear()}-${pad(month)}`));
  }

  onYear(year: number): void {
    this.value.set(
      this.clamp(`${year}-${pad(this.selectedMonth() || 1)}`)
    );
  }

  step(delta: number): void {
    const next = this.shift(delta);
    if (this.max() && next > this.max()) {
      return;
    }
    this.value.set(next);
  }

  private shift(delta: number): string {
    const total =
      this.selectedYear() * 12 + (this.selectedMonth() - 1) + delta;
    return `${Math.floor(total / 12)}-${pad((total % 12) + 1)}`;
  }

  private clamp(candidate: string): string {
    const max = this.max();
    return max && candidate > max ? max : candidate;
  }
}