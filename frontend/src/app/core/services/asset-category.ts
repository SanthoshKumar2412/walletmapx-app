import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface AssetCategoryResponse {
  id: number;
  name: string;
  description: string;
}

@Injectable({
  providedIn: 'root'
})
export class AssetCategoryService {

  private readonly apiUrl =
    `${environment.apiBaseUrl}/api/asset-categories`;

  constructor(
    private http: HttpClient
  ) {}

  getAllCategories(): Observable<AssetCategoryResponse[]> {

    return this.http.get<AssetCategoryResponse[]>(
      this.apiUrl
    );
  }
}