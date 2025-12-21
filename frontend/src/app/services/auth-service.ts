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
  initialized: boolean;
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
    initialized: false,
  });

  public currentUser = computed(() => this.authState().user);
  public isLoggedIn = computed(() => !!this.authState().user);
  public isInitialized = computed(() => this.authState().initialized);

    checkSession(): Observable<BackendAuthResponse | null> {
      return this.http.get<BackendAuthResponse>(`${this.API_URL}/me`).pipe(
        tap((response) => {
          this.authState.set({
            user: response.username,
            roles: response.roles,
            initialized: true,
          });
        }),
        catchError(() => {
          this.authState.set({
            user: null,
            roles: null,
            initialized: true,
          });
          return of(null);
        }),
      );
    }

  public getUserRoles(): Role[] | null {
    return this.authState().roles;
  }

  public hasRole(role: Role): boolean {
    const roles = this.authState().roles;
    if (!roles) return false;

    return roles.some(r => String(r) === String(role));
  }

  public isLibrarian(): boolean {
    return this.hasRole(Role.ROLE_LIBRARIAN) || this.hasRole(Role.ROLE_ADMIN);
  }

  login(credentials: { username: string; password: string }): Observable<BackendAuthResponse> {
    return this.http.post<BackendAuthResponse>(`${this.API_URL}/login`, credentials).pipe(
      tap((response) => {
        this.authState.set({
          user: response.username,
          roles: response.roles,
          initialized: true,
        });
        void this.router.navigate(['/profil']);
      }),
    );
  }

  private onLogoutCallbacks: (() => void)[] = [];

  registerLogoutCallback(callback: () => void): void {
    this.onLogoutCallbacks.push(callback);
  }

  logout() {
    this.http.post(`${this.API_URL}/logout`, {}).subscribe({
      next: () => {
        this.onLogoutCallbacks.forEach((cb) => cb());
        this.authState.set({ user: null, roles: null, initialized: true });
        void this.router.navigate(['/zaloguj-sie']);
      },
      error: () => {
        this.onLogoutCallbacks.forEach((cb) => cb());
        this.authState.set({ user: null, roles: null, initialized: true });
        void this.router.navigate(['/zaloguj-sie']);
      },
    });
  }
}
