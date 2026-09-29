import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface StatisticsResponse {

  totalIncome: number;

  totalExpenses: number;

  totalSavings: number;

  savingsRate: number;

  totalAssets: number;

  totalLiabilities: number;

  totalInvestments: number;

  netWorth: number;

  incomeByCategory: {
    [category: string]: number;
  };

  expenseByCategory: {
    [category: string]: number;
  };
}

export interface MonthlyTrendItem {

  month: string;

  totalIncome: number;

  totalExpenses: number;

  savings: number;
}

@Injectable({
  providedIn: 'root'
})
export class StatisticsService {

  private readonly apiUrl =
    'http://localhost:8082/api/statistics';

  constructor(
    private http: HttpClient
  ) {}

  // =========================================================
  // OVERVIEW
  // =========================================================

  getOverview(): Observable<StatisticsResponse> {

    return this.http.get<StatisticsResponse>(
      `${this.apiUrl}/overview`
    );
  }

  // =========================================================
  // MONTHLY TREND
  // =========================================================

  getTrend(
    months: number = 6
  ): Observable<MonthlyTrendItem[]> {

    return this.http.get<MonthlyTrendItem[]>(
      `${this.apiUrl}/trend`,
      {
        params: {
          months: months.toString()
        }
      }
    );
  }
}