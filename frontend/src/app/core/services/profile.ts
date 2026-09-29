import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProfileResponse {
  id: number;
  name: string;
  email: string;
  profileImageUrl: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

  private readonly apiUrl =
    'http://localhost:8082/api/profile';

  constructor(
    private http: HttpClient
  ) {}

  getMyProfile(): Observable<ProfileResponse> {

    return this.http.get<ProfileResponse>(
      this.apiUrl
    );
  }

  uploadProfileImage(
    file: File
  ): Observable<ProfileResponse> {

    const formData = new FormData();

    formData.append('file', file);

    return this.http.post<ProfileResponse>(
      `${this.apiUrl}/image`,
      formData
    );
  }
}