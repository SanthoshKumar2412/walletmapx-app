// src/app/features/statistics/statistics.ts
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';

import {
  StatisticsResponse,
  MonthlyTrendItem,
  StatisticsService
} from '../../core/services/statistics';

import {
  currentMonth,
  formatMonthLabel
} from '../../core/utils/date';

import { MonthPicker } from '../../shared/month-picker/month-picker';
import { IncomeExpenseChart } from '../../shared/income-expense-chart/income-expense-chart';

@Component({
  selector: 'app-statistics',
  standalone: true,
  imports: [CommonModule, MonthPicker, IncomeExpenseChart],
  templateUrl: './statistics.html'
})
export class Statistics implements OnInit, OnDestroy {

  private readonly statisticsService =
    inject(StatisticsService);

  statistics: StatisticsResponse | null = null;

  trend: MonthlyTrendItem[] = [];
  trendError = '';
  loading = true;
  errorMessage = '';

  /** Trend length (months back from today) */
  selectedMonths = 6;

  /** Month for the summary cards + category breakdown (YYYY-MM) */
  selectedMonth = currentMonth();

  readonly maxMonth = currentMonth();

  private overviewRequest?: Subscription;

  get monthLabel(): string {
    return formatMonthLabel(this.selectedMonth);
  }

  /** "2026-10" -> "October 2026" (used by the trend table) */
  formatTrendMonth(month: string): string {
    return formatMonthLabel(month);
  }

  ngOnInit(): void {
    this.loadOverview();
    this.loadTrend();
  }

  ngOnDestroy(): void {
    this.overviewRequest?.unsubscribe();
  }

  // =========================================================
  // OVERVIEW (selected month)
  // =========================================================

  loadOverview(): void {

    this.errorMessage = '';

    this.overviewRequest?.unsubscribe();

    this.overviewRequest = this.statisticsService
      .getOverview(this.selectedMonth)
      .subscribe({

        next: (response) => {
          this.statistics = response;
          this.loading = false;
        },

        error: (error) => {
          console.error('Statistics overview failed:', error);

          this.errorMessage =
            error?.error?.message || 'Unable to load statistics.';

          this.loading = false;
        }
      });
  }

  onMonthChange(value: string): void {

    this.selectedMonth = value || currentMonth();

    this.loadOverview();
  }

  // =========================================================
  // TREND
  // =========================================================

  loadTrend(): void {

    this.trendError = '';

    this.statisticsService.getTrend(this.selectedMonths).subscribe({

      next: (response) => {
        this.trend = response;
      },

      error: (error) => {
        console.error('Statistics trend failed:', error);

        this.trendError =
          error?.error?.message || 'Unable to load statistics trend.';
      }
    });
  }

  changeMonths(months: number): void {

    this.selectedMonths = months;

    this.loadTrend();
  }

  // =========================================================
  // GETTERS
  // =========================================================

  get totalIncome(): number {
    return this.statistics?.totalIncome ?? 0;
  }

  get totalExpenses(): number {
    return this.statistics?.totalExpenses ?? 0;
  }

  get totalSavings(): number {
    return this.statistics?.totalSavings ?? 0;
  }

  get savingsRate(): number {
    return this.statistics?.savingsRate ?? 0;
  }

  get netWorth(): number {
    return this.statistics?.netWorth ?? 0;
  }

  get totalAssets(): number {
    return this.statistics?.totalAssets ?? 0;
  }

  get totalLiabilities(): number {
    return this.statistics?.totalLiabilities ?? 0;
  }

  get totalInvestments(): number {
    return this.statistics?.totalInvestments ?? 0;
  }

  get incomeCategories() {
    return Object.entries(
      this.statistics?.incomeByCategory ?? {}
    );
  }

  get expenseCategories() {
    return Object.entries(
      this.statistics?.expenseByCategory ?? {}
    );
  }
}