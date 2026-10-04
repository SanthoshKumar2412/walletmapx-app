// src/app/core/services/dashboard.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface DashboardResponse {
  totalAssets: number;
  totalLiabilities: number;
  netWorth: number;
  totalIncome: number;
  totalExpenses: number;
  totalInvestments: number;
}

export interface YearlyNetWorthItem {
  month: string;
  netWorth: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private readonly apiUrl =
    `${environment.apiBaseUrl}/api/dashboard`;

  constructor(
    private http: HttpClient
  ) {}

  /**
   * month = "YYYY-MM". Income/expenses are for that month only;
   * assets, liabilities, investments and net worth are current.
   */
  getDashboard(month?: string): Observable<DashboardResponse> {

    let params = new HttpParams();

    if (month) {
      params = params.set('month', month);
    }

    return this.http.get<DashboardResponse>(
      this.apiUrl,
      { params }
    );
  }

  getYearlyNetWorth(): Observable<YearlyNetWorthItem[]> {

    return this.http.get<YearlyNetWorthItem[]>(
      `${this.apiUrl}/net-worth/yearly`
    );
  }
}