import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ProfileResponse {
  id: number;
  name: string;
  email: string;
  profileImageUrl: string | null;
}

@Injectable({ providedIn: 'root' })
export class ProfileService {

  private readonly apiUrl = `${environment.apiBaseUrl}/api/profile`;

  private readonly profileSubject =
    new BehaviorSubject<ProfileResponse | null>(null);

  /** Latest known profile (navbar listens to this). */
  readonly profile$ = this.profileSubject.asObservable();

  constructor(private http: HttpClient) {}

  getMyProfile(): Observable<ProfileResponse> {
    return this.http
      .get<ProfileResponse>(this.apiUrl)
      .pipe(tap(profile => this.profileSubject.next(profile)));
  }

  uploadProfileImage(file: File): Observable<ProfileResponse> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http
      .post<ProfileResponse>(`${this.apiUrl}/image`, formData)
      .pipe(tap(profile => this.profileSubject.next(profile)));
  }

  clear(): void {
    this.profileSubject.next(null);
  }
}