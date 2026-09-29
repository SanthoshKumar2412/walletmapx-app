import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ExpenseRequest {
  category: string;
  amount: number;
  expenseDate: string;
  description: string;
}

export interface ExpenseResponse {
  id: number;
  userId: number;
  category: string;
  amount: number;
  expenseDate: string;
  description: string | null;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class ExpenseService {

  private readonly apiUrl =
    'http://localhost:8082/api/expenses';

  constructor(
    private http: HttpClient
  ) {}

  getAllExpenses(): Observable<ExpenseResponse[]> {
    return this.http.get<ExpenseResponse[]>(
      this.apiUrl
    );
  }

  getExpenseById(
    id: number
  ): Observable<ExpenseResponse> {
    return this.http.get<ExpenseResponse>(
      `${this.apiUrl}/${id}`
    );
  }

  createExpense(
    request: ExpenseRequest
  ): Observable<ExpenseResponse> {
    return this.http.post<ExpenseResponse>(
      this.apiUrl,
      request
    );
  }

  updateExpense(
    id: number,
    request: ExpenseRequest
  ): Observable<ExpenseResponse> {
    return this.http.put<ExpenseResponse>(
      `${this.apiUrl}/${id}`,
      request
    );
  }

  deleteExpense(
    id: number
  ): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}