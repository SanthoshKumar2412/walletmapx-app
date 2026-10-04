import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface IncomeRequest {
  category: string;
  amount: number;
  incomeDate: string;
  description: string;
}

export interface IncomeResponse {
  id: number;
  category: string;
  amount: number;
  incomeDate: string;
  description: string | null;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class IncomeService {

  private readonly apiUrl =
    `${environment.apiBaseUrl}/api/income`;

  constructor(
    private http: HttpClient
  ) {}

  getAllIncome(): Observable<IncomeResponse[]> {

    return this.http.get<IncomeResponse[]>(
      this.apiUrl
    );
  }

  createIncome(
    request: IncomeRequest
  ): Observable<IncomeResponse> {

    return this.http.post<IncomeResponse>(
      this.apiUrl,
      request
    );
  }

  getIncomeById(
    id: number
  ): Observable<IncomeResponse> {

    return this.http.get<IncomeResponse>(
      `${this.apiUrl}/${id}`
    );
  }

  updateIncome(
    id: number,
    request: IncomeRequest
  ): Observable<IncomeResponse> {

    return this.http.put<IncomeResponse>(
      `${this.apiUrl}/${id}`,
      request
    );
  }

  deleteIncome(
    id: number
  ): Observable<void> {

    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}