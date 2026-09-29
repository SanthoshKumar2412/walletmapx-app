import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface InvestmentRequest {
  name: string;
  investmentType: string;
  quantity: number;
  buyPrice: number;
  investedAmount: number;
  currentValue: number | null;
  investmentDate: string;
  notes: string;
}

export interface InvestmentResponse {
  id: number;
  userId: number;
  name: string;
  investmentType: string;
  quantity: number;
  buyPrice: number;
  investedAmount: number;
  currentValue: number | null;
  investmentDate: string | null;
  notes: string | null;
  createdAt: string;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class InvestmentService {

  private readonly apiUrl =
    'http://localhost:8082/api/investments';

  constructor(
    private http: HttpClient
  ) {}

  // =========================================================
  // GET ALL
  // =========================================================

  getAllInvestments(): Observable<InvestmentResponse[]> {

    return this.http.get<InvestmentResponse[]>(
      this.apiUrl
    );
  }

  // =========================================================
  // GET BY ID
  // =========================================================

  getInvestmentById(
    id: number
  ): Observable<InvestmentResponse> {

    return this.http.get<InvestmentResponse>(
      `${this.apiUrl}/${id}`
    );
  }

  // =========================================================
  // CREATE
  // =========================================================

  createInvestment(
    request: InvestmentRequest
  ): Observable<InvestmentResponse> {

    return this.http.post<InvestmentResponse>(
      this.apiUrl,
      request
    );
  }

  // =========================================================
  // UPDATE
  // =========================================================

  updateInvestment(
    id: number,
    request: InvestmentRequest
  ): Observable<InvestmentResponse> {

    return this.http.put<InvestmentResponse>(
      `${this.apiUrl}/${id}`,
      request
    );
  }

  // =========================================================
  // DELETE
  // =========================================================

  deleteInvestment(
    id: number
  ): Observable<void> {

    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}