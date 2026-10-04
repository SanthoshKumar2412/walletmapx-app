// src/app/shared/net-worth-chart/net-worth-chart.ts
import {
  Component,
  DestroyRef,
  ElementRef,
  computed,
  effect,
  inject,
  input,
  signal,
  viewChild
} from '@angular/core';

import { DecimalPipe } from '@angular/common';

import {
  Chart,
  ChartConfiguration,
  registerables
} from 'chart.js';

import { YearlyNetWorthItem } from '../../core/services/dashboard';

Chart.register(...registerables);

type RangeKey = '6M' | '12M' | 'ALL';

const COMPACT = new Intl.NumberFormat('en-IN', {
  notation: 'compact',
  maximumFractionDigits: 1
});

const FULL = new Intl.NumberFormat('en-IN', {
  minimumFractionDigits: 2,
  maximumFractionDigits: 2
});

const pad = (n: number): string => String(n).padStart(2, '0');

@Component({
  selector: 'app-net-worth-chart',
  standalone: true,
  imports: [DecimalPipe],
  template: `
    <div>

      <!-- Header: current value, change, range tabs -->
      <div class="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">

        <div>
          <p class="text-sm font-medium text-gray-500">Net worth</p>

          <p class="mt-1 text-3xl font-bold text-gray-900">
            ₹{{ latest() | number:'1.0-2' }}
          </p>

          @if (change(); as c) {
            <p
              class="mt-1 text-sm font-semibold"
              [class.text-green-600]="c.diff >= 0"
              [class.text-red-600]="c.diff < 0"
            >
              {{ c.diff >= 0 ? '▲' : '▼' }}
              ₹{{ abs(c.diff) | number:'1.0-2' }}
              @if (c.pct !== null) {
                ({{ c.pct | number:'1.1-1' }}%)
              }
              <span class="font-normal text-gray-400">
                since {{ sinceLabel() }}
              </span>
            </p>
          }
        </div>

        <div class="inline-flex self-start rounded-lg bg-gray-100 p-1" role="tablist">
          @for (r of ranges; track r.key) {
            <button
              type="button"
              role="tab"
              [attr.aria-selected]="range() === r.key"
              (click)="range.set(r.key)"
              class="rounded-md px-3 py-1.5 text-xs font-semibold transition"
              [class.bg-white]="range() === r.key"
              [class.shadow-sm]="range() === r.key"
              [class.text-gray-900]="range() === r.key"
              [class.text-gray-500]="range() !== r.key"
            >
              {{ r.label }}
            </button>
          }
        </div>

      </div>

      <!-- Chart (canvas is always in the DOM) -->
      <div class="relative mt-5 h-64 w-full sm:h-72">

        <canvas #canvas class="h-full w-full"></canvas>

        @if (loading()) {
          <div class="absolute inset-0 flex items-center justify-center bg-white">
            <p class="text-sm text-gray-500">Loading net worth graph...</p>
          </div>
        } @else if (error()) {
          <div class="absolute inset-0 flex items-center justify-center bg-white">
            <p class="text-sm text-red-600">{{ error() }}</p>
          </div>
        } @else if (series().length === 0) {
          <div class="absolute inset-0 flex flex-col items-center justify-center bg-white">
            <p class="text-sm text-gray-500">No net worth history yet.</p>
            <p class="mt-1 text-xs text-gray-400">
              Your first monthly snapshot is created automatically.
            </p>
          </div>
        }

      </div>

      @if (!loading() && !error() && series().length === 1) {
        <p class="mt-2 text-xs text-gray-400">
          Only one month recorded so far. The trend line appears from next month.
        </p>
      }

    </div>
  `
})
export class NetWorthChart {

  readonly data = input<YearlyNetWorthItem[]>([]);
  readonly loading = input<boolean>(false);
  readonly error = input<string>('');

  readonly ranges: { key: RangeKey; label: string }[] = [
    { key: '6M', label: '6M' },
    { key: '12M', label: '1Y' },
    { key: 'ALL', label: 'All' }
  ];

  readonly range = signal<RangeKey>('12M');

  private readonly canvas =
    viewChild<ElementRef<HTMLCanvasElement>>('canvas');

  private chart: Chart | null = null;

  /** Snapshots inside the selected range, oldest first */
  readonly series = computed(() => {

    const sorted = [...this.data()].sort(
      (a, b) => a.month.localeCompare(b.month)
    );

    const range = this.range();

    if (range === 'ALL') {
      return sorted;
    }

    const cutoff = this.cutoff(range === '6M' ? 6 : 12);

    return sorted.filter(item => item.month.slice(0, 7) >= cutoff);
  });

  readonly latest = computed(
    () => this.series().at(-1)?.netWorth ?? 0
  );

  /** Change from the first to the last point of the range */
  readonly change = computed(() => {

    const points = this.series();

    if (points.length < 2) {
      return null;
    }

    const first = points[0].netWorth;
    const last = points[points.length - 1].netWorth;

    return {
      diff: last - first,
      pct: first !== 0 ? ((last - first) / Math.abs(first)) * 100 : null
    };
  });

  readonly sinceLabel = computed(() => {
    const first = this.series()[0];
    return first ? this.formatMonth(first.month) : '';
  });

  constructor() {

    effect(() => {

      const canvas = this.canvas()?.nativeElement;
      const points = this.series();

      this.chart?.destroy();
      this.chart = null;

      if (!canvas || points.length === 0) {
        return;
      }

      this.chart = new Chart(canvas, this.buildConfig(points));
    });

    inject(DestroyRef).onDestroy(() => this.chart?.destroy());
  }

  abs(value: number): number {
    return Math.abs(value);
  }

  // =========================================================
  // CHART CONFIG
  // =========================================================

  private buildConfig(
    points: YearlyNetWorthItem[]
  ): ChartConfiguration<'line'> {

    const single = points.length === 1;

    return {

      type: 'line',

      data: {
        labels: points.map(p => this.formatMonth(p.month)),

        datasets: [
          {
            label: 'Net worth',
            data: points.map(p => p.netWorth),

            borderColor: '#2563eb',
            borderWidth: 2.5,
            tension: 0.4,
            fill: true,

            // soft gradient under the line
            backgroundColor: (context) => {

              const { chart } = context;
              const { ctx, chartArea } = chart;

              if (!chartArea) {
                return 'rgba(37, 99, 235, 0.15)';
              }

              const gradient = ctx.createLinearGradient(
                0, chartArea.top, 0, chartArea.bottom
              );

              gradient.addColorStop(0, 'rgba(37, 99, 235, 0.28)');
              gradient.addColorStop(1, 'rgba(37, 99, 235, 0)');

              return gradient;
            },

            // clean line; dots only on hover (or when a single point)
            pointRadius: single ? 5 : 0,
            pointHoverRadius: 6,
            pointBackgroundColor: '#2563eb',
            pointBorderColor: '#ffffff',
            pointBorderWidth: 2
          }
        ]
      },

      options: {
        responsive: true,
        maintainAspectRatio: false,

        interaction: { mode: 'index', intersect: false },

        layout: { padding: { top: 8, right: 8 } },

        plugins: {
          legend: { display: false },

          tooltip: {
            backgroundColor: '#0f172a',
            padding: 10,
            displayColors: false,
            titleFont: { weight: 'bold' },
            callbacks: {
              label: (context) =>
                `₹${FULL.format(Number(context.raw ?? 0))}`
            }
          }
        },

        scales: {
          x: {
            grid: { display: false },
            border: { display: false },
            ticks: { maxTicksLimit: 6, color: '#9ca3af' }
          },

          y: {
            beginAtZero: false,
            grace: '12%',
            border: { display: false },
            grid: { color: '#eef2f7' },
            ticks: {
              maxTicksLimit: 5,
              color: '#9ca3af',
              callback: (value) => '₹' + COMPACT.format(Number(value))
            }
          }
        }
      }
    };
  }

  // =========================================================
  // HELPERS
  // =========================================================

  /** First month (YYYY-MM) that is still inside an N-month window */
  private cutoff(months: number): string {

    const now = new Date();

    const start = new Date(
      now.getFullYear(),
      now.getMonth() - (months - 1),
      1
    );

    return `${start.getFullYear()}-${pad(start.getMonth() + 1)}`;
  }

  private formatMonth(month: string): string {

    const date = new Date(`${month}T00:00:00`);

    if (Number.isNaN(date.getTime())) {
      return month;
    }

    return date.toLocaleDateString('en-IN', {
      month: 'short',
      year: 'numeric'
    });
  }
}