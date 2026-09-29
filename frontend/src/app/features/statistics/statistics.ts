import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';

import {
  StatisticsResponse,
  MonthlyTrendItem,
  StatisticsService
} from '../../core/services/statistics';

@Component({
  selector: 'app-statistics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './statistics.html'
})
export class Statistics implements OnInit {

  private readonly statisticsService =
    inject(StatisticsService);

  statistics: StatisticsResponse | null = null;

  trend: MonthlyTrendItem[] = [];

  loading = true;
  errorMessage = '';

  selectedMonths = 6;

  ngOnInit(): void {
    this.loadStatistics();
  }

  // =========================================================
  // LOAD STATISTICS
  // =========================================================

  loadStatistics(): void {

    this.loading = true;
    this.errorMessage = '';

    this.statisticsService
      .getOverview()
      .subscribe({

        next: (response) => {

          console.log(
            'Statistics overview:',
            response
          );

          this.statistics = response;

          this.loadTrend();
        },

        error: (error) => {

          console.error(
            'Statistics overview failed:',
            error
          );

          this.errorMessage =
            error?.error?.message ||
            'Unable to load statistics.';

          this.loading = false;
        }

      });
  }

  // =========================================================
  // LOAD TREND
  // =========================================================

  loadTrend(): void {

    this.statisticsService
      .getTrend(this.selectedMonths)
      .subscribe({

        next: (response) => {

          console.log(
            'Statistics trend:',
            response
          );

          this.trend = response;

          this.loading = false;
        },

        error: (error) => {

          console.error(
            'Statistics trend failed:',
            error
          );

          this.errorMessage =
            error?.error?.message ||
            'Unable to load statistics trend.';

          this.loading = false;
        }

      });
  }

  // =========================================================
  // CHANGE TREND PERIOD
  // =========================================================

  changeMonths(months: number): void {

    this.selectedMonths = months;

    this.loadTrend();
  }

  // =========================================================
  // TOTAL INCOME
  // =========================================================

  get totalIncome(): number {
    return this.statistics?.totalIncome ?? 0;
  }

  // =========================================================
  // TOTAL EXPENSES
  // =========================================================

  get totalExpenses(): number {
    return this.statistics?.totalExpenses ?? 0;
  }

  // =========================================================
  // TOTAL SAVINGS
  // =========================================================

  get totalSavings(): number {
    return this.statistics?.totalSavings ?? 0;
  }

  // =========================================================
  // SAVINGS RATE
  // =========================================================

  get savingsRate(): number {
    return this.statistics?.savingsRate ?? 0;
  }

  // =========================================================
  // NET WORTH
  // =========================================================

  get netWorth(): number {
    return this.statistics?.netWorth ?? 0;
  }

  // =========================================================
  // ASSETS
  // =========================================================

  get totalAssets(): number {
    return this.statistics?.totalAssets ?? 0;
  }

  // =========================================================
  // LIABILITIES
  // =========================================================

  get totalLiabilities(): number {
    return this.statistics?.totalLiabilities ?? 0;
  }

  // =========================================================
  // INVESTMENTS
  // =========================================================

  get totalInvestments(): number {
    return this.statistics?.totalInvestments ?? 0;
  }

  // =========================================================
  // CATEGORY DATA
  // =========================================================

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