import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AssetRequest {
  categoryId: number | null;
  name: string;
  institution: string;
  investedAmount: number;
  currentValue: number;
  purchaseDate: string;
  notes: string;
}

export interface AssetResponse {
  id: number;
  userId: number;
  categoryId: number | null;
  name: string;
  institution: string;
  investedAmount: number;
  currentValue: number;
  purchaseDate: string;
  notes: string;
  createdAt: string;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class AssetService {

  private readonly apiUrl =
    'http://localhost:8082/api/assets';

  constructor(
    private http: HttpClient
  ) {}

  getAllAssets(): Observable<AssetResponse[]> {
    return this.http.get<AssetResponse[]>(
      this.apiUrl
    );
  }

  getAssetById(
    id: number
  ): Observable<AssetResponse> {

    return this.http.get<AssetResponse>(
      `${this.apiUrl}/${id}`
    );
  }

  createAsset(
    request: AssetRequest
  ): Observable<AssetResponse> {

    return this.http.post<AssetResponse>(
      this.apiUrl,
      request
    );
  }

  updateAsset(
    id: number,
    request: AssetRequest
  ): Observable<AssetResponse> {

    return this.http.put<AssetResponse>(
      `${this.apiUrl}/${id}`,
      request
    );
  }

  deleteAsset(
    id: number
  ): Observable<void> {

    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }

  getAssetsByCategory(
    categoryId: number
  ): Observable<AssetResponse[]> {

    return this.http.get<AssetResponse[]>(
      `${this.apiUrl}/category/${categoryId}`
    );
  }
}