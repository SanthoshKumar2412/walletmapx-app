import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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
    'http://localhost:8082/api/asset-categories';

  constructor(
    private http: HttpClient
  ) {}

  getAllCategories(): Observable<AssetCategoryResponse[]> {

    return this.http.get<AssetCategoryResponse[]>(
      this.apiUrl
    );
  }
}