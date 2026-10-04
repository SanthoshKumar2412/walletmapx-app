// src/app/shared/income-expense-chart/income-expense-chart.ts
import {
  Component,
  DestroyRef,
  ElementRef,
  computed,
  effect,
  inject,
  input,
  viewChild
} from '@angular/core';

import { DecimalPipe } from '@angular/common';

import {
  Chart,
  ChartConfiguration,
  registerables
} from 'chart.js';

import { MonthlyTrendItem } from '../../core/services/statistics';

Chart.register(...registerables);

const COMPACT = new Intl.NumberFormat('en-IN', {
  notation: 'compact',
  maximumFractionDigits: 1
});

const FULL = new Intl.NumberFormat('en-IN', {
  minimumFractionDigits: 2,
  maximumFractionDigits: 2
});

@Component({
  selector: 'app-income-expense-chart',
  standalone: true,
  imports: [DecimalPipe],
  template: `
    <div>

      <!-- Period totals -->
      <div class="grid grid-cols-3 gap-3">

        <div class="rounded-lg bg-green-50 p-3">
          <p class="text-xs text-green-700">Income</p>
          <p class="mt-1 text-sm font-bold text-green-700 sm:text-base">
            ₹{{ totalIncome() | number:'1.0-0' }}
          </p>
        </div>

        <div class="rounded-lg bg-red-50 p-3">
          <p class="text-xs text-red-700">Expenses</p>
          <p class="mt-1 text-sm font-bold text-red-700 sm:text-base">
            ₹{{ totalExpenses() | number:'1.0-0' }}
          </p>
        </div>

        <div class="rounded-lg bg-blue-50 p-3">
          <p class="text-xs text-blue-700">Saved</p>
          <p
            class="mt-1 text-sm font-bold sm:text-base"
            [class.text-blue-700]="totalSavings() >= 0"
            [class.text-red-700]="totalSavings() < 0"
          >
            ₹{{ totalSavings() | number:'1.0-0' }}
          </p>
        </div>

      </div>

      <!-- Chart -->
      <div class="relative mt-5 h-64 w-full sm:h-72">

        <canvas #canvas class="h-full w-full"></canvas>

        @if (!hasData()) {
          <div class="absolute inset-0 flex items-center justify-center bg-white">
            <p class="text-sm text-gray-500">
              No income or expenses recorded in this period.
            </p>
          </div>
        }

      </div>

    </div>
  `
})
export class IncomeExpenseChart {

  readonly data = input<MonthlyTrendItem[]>([]);

  private readonly canvas =
    viewChild<ElementRef<HTMLCanvasElement>>('canvas');

  private chart: Chart | null = null;

  readonly totalIncome = computed(() =>
    this.data().reduce((sum, item) => sum + Number(item.totalIncome || 0), 0)
  );

  readonly totalExpenses = computed(() =>
    this.data().reduce((sum, item) => sum + Number(item.totalExpenses || 0), 0)
  );

  readonly totalSavings = computed(
    () => this.totalIncome() - this.totalExpenses()
  );

  readonly hasData = computed(
    () => this.totalIncome() > 0 || this.totalExpenses() > 0
  );

  constructor() {

    effect(() => {

      const canvas = this.canvas()?.nativeElement;
      const items = this.data();

      this.chart?.destroy();
      this.chart = null;

      if (!canvas || items.length === 0) {
        return;
      }

      this.chart = new Chart(canvas, this.buildConfig(items));
    });

    inject(DestroyRef).onDestroy(() => this.chart?.destroy());
  }

  // =========================================================
  // CHART CONFIG
  // =========================================================

  private buildConfig(items: MonthlyTrendItem[]): ChartConfiguration {

    return {

      type: 'bar',

      data: {
        labels: items.map(item => this.formatMonth(item.month)),

        datasets: [
          {
            type: 'bar',
            label: 'Income',
            data: items.map(item => Number(item.totalIncome)),
            backgroundColor: '#16a34a',
            borderRadius: 6,
            maxBarThickness: 26,
            order: 2
          },
          {
            type: 'bar',
            label: 'Expenses',
            data: items.map(item => Number(item.totalExpenses)),
            backgroundColor: '#ef4444',
            borderRadius: 6,
            maxBarThickness: 26,
            order: 2
          },
          {
            type: 'line',
            label: 'Savings',
            data: items.map(item => Number(item.savings)),
            borderColor: '#2563eb',
            backgroundColor: '#2563eb',
            borderWidth: 2.5,
            tension: 0.35,
            pointRadius: 3,
            pointHoverRadius: 5,
            pointBorderColor: '#ffffff',
            pointBorderWidth: 2,
            order: 1
          }
        ]
      },

      options: {
        responsive: true,
        maintainAspectRatio: false,

        interaction: { mode: 'index', intersect: false },

        plugins: {
          legend: {
            position: 'top',
            align: 'end',
            labels: {
              usePointStyle: true,
              boxWidth: 8,
              color: '#6b7280'
            }
          },

          tooltip: {
            backgroundColor: '#0f172a',
            padding: 10,
            callbacks: {
              label: (context) =>
                ` ${context.dataset.label}: ₹${FULL.format(Number(context.raw ?? 0))}`
            }
          }
        },

        scales: {
          x: {
            grid: { display: false },
            border: { display: false },
            ticks: { color: '#9ca3af' }
          },

          y: {
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

  /** "2026-10" -> "Oct 26" */
  private formatMonth(month: string): string {

    const [year, mon] = month.split('-').map(Number);

    return new Date(year, mon - 1, 1).toLocaleDateString('en-IN', {
      month: 'short',
      year: '2-digit'
    });
  }
}