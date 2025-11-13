import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, Observable, of, tap } from 'rxjs';
import { Role } from '../types';
import { environment } from '../../environments/environment';
interface BackendAuthResponse {
  token: null;
  roles: Role[];
  username: string;
  responseType: string;
}
interface AuthState {
  user: string | null;
  roles: Role[] | null;
}
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private API_URL = environment.apiUrl + 'auth';

  private authState = signal<AuthState>({
    user: null,
    roles: null,
  });

  public currentUser = computed(() => this.authState().user);
  public isLoggedIn = computed(() => !!this.authState().user);

  login(credentials: { username: string; password: string }): Observable<BackendAuthResponse> {
    return this.http.post<BackendAuthResponse>(`${this.API_URL}/login`, credentials).pipe(
      tap((response) => {
        this.authState.set({
          user: response.username,
          roles: response.roles,
        });
        void this.router.navigate(['/profil']);
      }),
      catchError((error) => {
        console.error('Błąd logowania:', error);
        return of(error);
      }),
    );
  }

  logout() {
    this.authState.set({ user: null, roles: null });
    void this.router.navigate(['/login']);
  }
}
