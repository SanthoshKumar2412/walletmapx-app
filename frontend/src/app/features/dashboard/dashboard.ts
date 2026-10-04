// src/app/features/dashboard/dashboard.ts
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { Subscription } from 'rxjs';

import {
  DashboardResponse,
  DashboardService,
  YearlyNetWorthItem
} from '../../core/services/dashboard';

import {
  MonthlyTrendItem,
  StatisticsService
} from '../../core/services/statistics';

import {
  currentMonth,
  formatMonthLabel
} from '../../core/utils/date';

import { MonthPicker } from '../../shared/month-picker/month-picker';
import { NetWorthChart } from '../../shared/net-worth-chart/net-worth-chart';
import { IncomeExpenseChart } from '../../shared/income-expense-chart/income-expense-chart';

/** Months shown in the dashboard income/expense chart */
const TREND_MONTHS = 6;

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    DecimalPipe,
    MonthPicker,
    NetWorthChart,
    IncomeExpenseChart
  ],
  templateUrl: './dashboard.html'
})
export class Dashboard implements OnInit, OnDestroy {

  private readonly dashboardService = inject(DashboardService);
  private readonly statisticsService = inject(StatisticsService);

  // =========================================================
  // DATA
  // =========================================================

  dashboard: DashboardResponse | null = null;

  yearlyNetWorth: YearlyNetWorthItem[] = [];

  trend: MonthlyTrendItem[] = [];

  /** Month shown in the cards (YYYY-MM). Defaults to this month. */
  selectedMonth = currentMonth();

  readonly maxMonth = currentMonth();

  readonly trendMonths = TREND_MONTHS;

  loading = true;
  graphLoading = true;
  trendLoading = true;

  errorMessage = '';
  graphErrorMessage = '';
  trendErrorMessage = '';

  private dashboardRequest?: Subscription;

  get monthLabel(): string {
    return formatMonthLabel(this.selectedMonth);
  }

  // =========================================================
  // INIT / DESTROY
  // =========================================================

  ngOnInit(): void {
    this.loadDashboard(true);
    this.loadYearlyNetWorth();
    this.loadTrend();
  }

  ngOnDestroy(): void {
    this.dashboardRequest?.unsubscribe();
  }

  // =========================================================
  // MONTH PICKER (cards only; charts always show history)
  // =========================================================

  onMonthChange(value: string): void {

    this.selectedMonth = value || currentMonth();

    this.loadDashboard(false);
  }

  // =========================================================
  // LOADERS
  // =========================================================

  private loadDashboard(initial: boolean): void {

    if (initial) {
      this.loading = true;
    }

    this.errorMessage = '';

    // Cancel the previous request if the month changes quickly
    this.dashboardRequest?.unsubscribe();

    this.dashboardRequest = this.dashboardService
      .getDashboard(this.selectedMonth)
      .subscribe({

        next: (response) => {
          this.dashboard = response;
          this.loading = false;
        },

        error: (error) => {
          console.error('Dashboard loading failed:', error);

          this.errorMessage =
            error?.error?.message || 'Unable to load dashboard.';

          this.loading = false;
        }
      });
  }

  private loadYearlyNetWorth(): void {

    this.graphLoading = true;
    this.graphErrorMessage = '';

    this.dashboardService.getYearlyNetWorth().subscribe({

      next: (response) => {
        this.yearlyNetWorth = response;
        this.graphLoading = false;
      },

      error: (error) => {
        console.error('Yearly net worth loading failed:', error);

        this.graphErrorMessage =
          error?.error?.message || 'Unable to load net worth graph.';

        this.graphLoading = false;
      }
    });
  }

  private loadTrend(): void {

    this.trendLoading = true;
    this.trendErrorMessage = '';

    this.statisticsService.getTrend(TREND_MONTHS).subscribe({

      next: (response) => {
        this.trend = response;
        this.trendLoading = false;
      },

      error: (error) => {
        console.error('Income/expense trend failed:', error);

        this.trendErrorMessage =
          error?.error?.message || 'Unable to load the income and expense graph.';

        this.trendLoading = false;
      }
    });
  }
}