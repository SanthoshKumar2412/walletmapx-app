import { Injectable } from '@angular/core';

import {
  HttpClient
} from '@angular/common/http';

import {
  Observable
} from 'rxjs';

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
    'http://localhost:8082/api/dashboard';

  constructor(
    private http: HttpClient
  ) {}

  // =========================================================
  // MAIN DASHBOARD
  // =========================================================

  getDashboard():
    Observable<DashboardResponse> {

    return this.http.get<DashboardResponse>(
      this.apiUrl
    );
  }

  // =========================================================
  // YEARLY NET WORTH
  // =========================================================

  getYearlyNetWorth():
    Observable<YearlyNetWorthItem[]> {

    return this.http.get<YearlyNetWorthItem[]>(
      `${this.apiUrl}/net-worth/yearly`
    );
  }
}