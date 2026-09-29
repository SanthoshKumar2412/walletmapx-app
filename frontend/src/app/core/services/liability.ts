import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LiabilityRequest {
  name: string;
  liabilityType: string;
  principalAmount: number;
  outstandingAmount: number;
  interestRate: number;
  monthlyEmi: number;
  startDate: string;
  endDate: string;
  notes: string;
}

export interface LiabilityResponse {
  id: number;
  userId: number;
  name: string;
  liabilityType: string;
  principalAmount: number;
  outstandingAmount: number;
  interestRate: number;
  monthlyEmi: number;
  startDate: string | null;
  endDate: string | null;
  notes: string | null;
  createdAt: string;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class LiabilityService {

  private readonly apiUrl =
    'http://localhost:8082/api/liabilities';

  constructor(
    private http: HttpClient
  ) {}

  getAllLiabilities(): Observable<LiabilityResponse[]> {
    return this.http.get<LiabilityResponse[]>(
      this.apiUrl
    );
  }

  getLiabilityById(
    id: number
  ): Observable<LiabilityResponse> {

    return this.http.get<LiabilityResponse>(
      `${this.apiUrl}/${id}`
    );
  }

  createLiability(
    request: LiabilityRequest
  ): Observable<LiabilityResponse> {

    return this.http.post<LiabilityResponse>(
      this.apiUrl,
      request
    );
  }

  updateLiability(
    id: number,
    request: LiabilityRequest
  ): Observable<LiabilityResponse> {

    return this.http.put<LiabilityResponse>(
      `${this.apiUrl}/${id}`,
      request
    );
  }

  deleteLiability(
    id: number
  ): Observable<void> {

    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}