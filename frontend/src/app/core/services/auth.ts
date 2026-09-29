import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  userId: number;
  name: string;
  email: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root',
})
export class Auth {

  private readonly API_URL = 'http://localhost:8082/api/auth';

  constructor(private http: HttpClient) {}

  // ==============================
  // LOGIN
  // ==============================

  login(
    request: LoginRequest
  ): Observable<ApiResponse<AuthResponse>> {

    return this.http
      .post<ApiResponse<AuthResponse>>(
        `${this.API_URL}/login`,
        request
      )
      .pipe(
        tap(response => {

          if (response.success && response.data) {

            localStorage.setItem(
              'token',
              response.data.token
            );

            localStorage.setItem(
              'user',
              JSON.stringify(response.data)
            );
          }

        })
      );
  }

  // ==============================
  // LOGOUT
  // ==============================

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  // ==============================
  // GET TOKEN
  // ==============================

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // ==============================
  // GET CURRENT USER
  // ==============================

  getUser(): AuthResponse | null {

    const user = localStorage.getItem('user');

    if (!user) {
      return null;
    }

    try {
      return JSON.parse(user) as AuthResponse;
    } catch {
      return null;
    }
  }

  // ==============================
  // LOGIN STATUS
  // ==============================

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}