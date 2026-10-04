
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

// =========================================================
// LOGIN REQUEST
// =========================================================

export interface LoginRequest {
  email: string;
  password: string;
}

// =========================================================
// REGISTER REQUEST
// =========================================================

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

// =========================================================
// AUTH RESPONSE
// =========================================================

export interface AuthResponse {
  token: string;
  tokenType: string;
  userId: number;
  name: string;
  email: string;
}

// =========================================================
// API RESPONSE
// =========================================================

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

// =========================================================
// AUTH SERVICE
// =========================================================

@Injectable({
  providedIn: 'root'
})
export class Auth {

  private readonly API_URL =
    `${environment.apiBaseUrl}/api/auth`;

  constructor(
    private readonly http: HttpClient
  ) {}

  // =========================================================
  // REGISTER
  // =========================================================

  register(
    request: RegisterRequest
  ): Observable<ApiResponse<string>> {

    return this.http.post<ApiResponse<string>>(
      `${this.API_URL}/register`,
      request
    );
  }

  // =========================================================
  // LOGIN
  // =========================================================

  login(
    request: LoginRequest
  ): Observable<ApiResponse<AuthResponse>> {

    return this.http
      .post<ApiResponse<AuthResponse>>(
        `${this.API_URL}/login`,
        request
      )
      .pipe(

        tap((response) => {

          if (
            response.success &&
            response.data
          ) {

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

  // =========================================================
  // LOGOUT
  // =========================================================

  logout(): void {

    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  // =========================================================
  // GET TOKEN
  // =========================================================

  getToken(): string | null {

    return localStorage.getItem('token');
  }

  // =========================================================
  // GET CURRENT USER
  // =========================================================

  getUser(): AuthResponse | null {

    const user =
      localStorage.getItem('user');

    if (!user) {
      return null;
    }

    try {

      const parsedUser: unknown =
        JSON.parse(user);

      return parsedUser as AuthResponse;

    } catch {

      return null;
    }
  }

  // =========================================================
  // LOGIN STATUS
  // =========================================================

  isLoggedIn(): boolean {

    return !!this.getToken();
  }
}
