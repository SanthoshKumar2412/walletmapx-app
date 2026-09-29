import {
  AfterViewInit,
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild,
  inject
} from '@angular/core';

import { DecimalPipe } from '@angular/common';

import {
  Chart,
  ChartConfiguration,
  registerables
} from 'chart.js';

import {
  DashboardResponse,
  DashboardService,
  YearlyNetWorthItem
} from '../../core/services/dashboard';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    DecimalPipe
  ],
  templateUrl: './dashboard.html'
})
export class Dashboard
  implements OnInit, AfterViewInit, OnDestroy {

  private readonly dashboardService =
    inject(DashboardService);

  // =========================================================
  // CANVAS
  // =========================================================

  @ViewChild('netWorthChart')
  netWorthChartCanvas?: ElementRef<HTMLCanvasElement>;

  // =========================================================
  // DATA
  // =========================================================

  dashboard: DashboardResponse | null = null;

  yearlyNetWorth: YearlyNetWorthItem[] = [];

  loading = true;

  graphLoading = true;

  errorMessage = '';

  graphErrorMessage = '';

  private netWorthChart: Chart<'line'> | null = null;

  private viewReady = false;

  // =========================================================
  // INIT
  // =========================================================

  ngOnInit(): void {

    this.loadDashboard();

    this.loadYearlyNetWorth();
  }

  // =========================================================
  // VIEW INIT
  // =========================================================

  ngAfterViewInit(): void {

    this.viewReady = true;

    this.tryCreateChart();
  }

  // =========================================================
  // DESTROY
  // =========================================================

  ngOnDestroy(): void {

    this.destroyChart();
  }

  // =========================================================
  // LOAD DASHBOARD
  // =========================================================

  private loadDashboard(): void {

    this.loading = true;

    this.errorMessage = '';

    this.dashboardService
      .getDashboard()
      .subscribe({

        next: (response) => {

          console.log(
            'Dashboard loaded:',
            response
          );

          this.dashboard = response;

          this.loading = false;
        },

        error: (error) => {

          console.error(
            'Dashboard loading failed:',
            error
          );

          this.errorMessage =
            error?.error?.message ||
            'Unable to load dashboard.';

          this.loading = false;
        }

      });
  }

  // =========================================================
  // LOAD YEARLY NET WORTH
  // =========================================================

  private loadYearlyNetWorth(): void {

    this.graphLoading = true;

    this.graphErrorMessage = '';

    this.dashboardService
      .getYearlyNetWorth()
      .subscribe({

        next: (response) => {

          console.log(
            'Yearly net worth:',
            response
          );

          this.yearlyNetWorth = response;

          this.graphLoading = false;

          /*
           * Canvas is inside @else block.
           * Wait for Angular to render it.
           */
          setTimeout(() => {

            this.tryCreateChart();

          }, 0);
        },

        error: (error) => {

          console.error(
            'Yearly net worth loading failed:',
            error
          );

          this.graphErrorMessage =
            error?.error?.message ||
            'Unable to load net worth graph.';

          this.graphLoading = false;
        }

      });
  }

  // =========================================================
  // TRY CREATE CHART
  // =========================================================

  private tryCreateChart(): void {

    if (!this.viewReady) {
      return;
    }

    if (this.graphLoading) {
      return;
    }

    if (this.yearlyNetWorth.length === 0) {
      return;
    }

    const canvas =
      this.netWorthChartCanvas?.nativeElement;

    if (!canvas) {

      console.warn(
        'Net worth canvas is not ready yet.'
      );

      return;
    }

    this.createNetWorthChart(canvas);
  }

  // =========================================================
  // CREATE NET WORTH CHART
  // =========================================================

  private createNetWorthChart(
    canvas: HTMLCanvasElement
  ): void {

    this.destroyChart();

    /*
     * Backend returns:
     *
     * [
     *   {
     *     month: "2026-09-01",
     *     netWorth: 21000
     *   }
     * ]
     *
     * Sort oldest -> newest.
     */

    const sortedData =
      [...this.yearlyNetWorth].sort(
        (a, b) =>
          a.month.localeCompare(b.month)
      );

    const labels =
      sortedData.map(
        item => this.formatMonth(item.month)
      );

    const values =
      sortedData.map(
        item => item.netWorth
      );

    const configuration:
      ChartConfiguration<'line'> = {

      type: 'line',

      data: {

        labels,

        datasets: [

          {
            label: 'Net Worth',

            data: values,

            fill: true,

            tension: 0.35,

            borderWidth: 2,

            pointRadius: 4,

            pointHoverRadius: 6
          }

        ]

      },

      options: {

        responsive: true,

        maintainAspectRatio: false,

        interaction: {

          mode: 'index',

          intersect: false
        },

        plugins: {

          legend: {

            display: true,

            position: 'top'
          },

          tooltip: {

            callbacks: {

              label: (context) => {

                const value =
                  context.parsed.y ?? 0;

                return ` Net Worth: ₹${value.toLocaleString(
                  'en-IN',
                  {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2
                  }
                )}`;
              }

            }

          }

        },

        scales: {

          x: {

            grid: {

              display: false
            }

          },

          y: {

            beginAtZero: false,

            ticks: {

              callback: (value) => {

                return '₹' +
                  Number(value).toLocaleString(
                    'en-IN'
                  );
              }

            }

          }

        }

      }

    };

    this.netWorthChart =
      new Chart(
        canvas,
        configuration
      );
  }

  // =========================================================
  // FORMAT MONTH
  // =========================================================

  private formatMonth(
    month: string
  ): string {

    /*
     * LocalDate from Spring Boot is serialized as:
     *
     * 2026-09-01
     */

    const date =
      new Date(`${month}T00:00:00`);

    if (Number.isNaN(date.getTime())) {

      return month;
    }

    return date.toLocaleDateString(
      'en-IN',
      {
        month: 'short',
        year: 'numeric'
      }
    );
  }

  // =========================================================
  // DESTROY CHART
  // =========================================================

  private destroyChart(): void {

    if (this.netWorthChart) {

      this.netWorthChart.destroy();

      this.netWorthChart = null;
    }
  }
}