// src/app/core/services/statistics.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface StatisticsResponse {
  totalIncome: number;
  totalExpenses: number;
  totalSavings: number;
  savingsRate: number;
  totalAssets: number;
  totalLiabilities: number;
  totalInvestments: number;
  netWorth: number;
  incomeByCategory: { [category: string]: number };
  expenseByCategory: { [category: string]: number };
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
    `${environment.apiBaseUrl}/api/statistics`;

  constructor(
    private http: HttpClient
  ) {}

  /** Income/expenses/categories for one month (YYYY-MM). */
  getOverview(month?: string): Observable<StatisticsResponse> {

    let params = new HttpParams();

    if (month) {
      params = params.set('month', month);
    }

    return this.http.get<StatisticsResponse>(
      `${this.apiUrl}/overview`,
      { params }
    );
  }

  getTrend(months: number = 6): Observable<MonthlyTrendItem[]> {

    return this.http.get<MonthlyTrendItem[]>(
      `${this.apiUrl}/trend`,
      { params: { months: months.toString() } }
    );
  }
}