import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, Observable, of, tap, switchMap, map } from 'rxjs';
import { LibraryBranch, Role } from '../types';
import { environment } from '../../environments/environment';
import { BranchService } from './branch.service';
interface BackendAuthResponse {
  token: null;
  roles: Role[];
  username: string;
  responseType: string;
  employeeOfBranch: LibraryBranch | null;
}
interface AuthState {
  user: string | null;
  roles: Role[] | null;
  initialized: boolean;
  employeeOfBranch: LibraryBranch | null;
}
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private branchService = inject(BranchService);

  private API_URL = environment.apiUrl + 'auth';

  private authState = signal<AuthState>({
    user: null,
    roles: null,
    initialized: false,
    employeeOfBranch: null,
  });

  public currentUser = computed(() => this.authState().user);
  public isLoggedIn = computed(() => !!this.authState().user);
  public isInitialized = computed(() => this.authState().initialized);
  public employeeOfBranch = computed(() => this.authState().employeeOfBranch);

  checkSession(): Observable<BackendAuthResponse | null> {
    return this.http.get<any>(`${this.API_URL}/me`).pipe(
      switchMap((response) => {
        const branchData = response.employeeOfBranch;
        // If it's a number (ID), we need to resolve it
        if (branchData && typeof branchData === 'number') {
          return this.branchService.loadBranches().pipe(
            map(() => {
              const fullBranch = this.branchService.getBranchFromStore(branchData);
              return { ...response, employeeOfBranch: fullBranch || null };
            })
          );
        }
        // If it looks like an object or is null, return as is
        return of(response);
      }),
      tap((response: BackendAuthResponse) => {
        this.authState.set({
          user: response.username,
          roles: response.roles,
          initialized: true,
          employeeOfBranch: response.employeeOfBranch,
        });
      }),
      catchError(() => {
        this.authState.set({
          user: null,
          roles: null,
          initialized: true,
          employeeOfBranch: null,
        });
        return of(null);
      })
    );
  }

  public getUserRoles(): Role[] | null {
    return this.authState().roles;
  }

  public getAuthState(): AuthState {
    return this.authState();
  }

  public hasRole(role: Role): boolean {
    const roles = this.authState().roles;
    if (!roles) return false;

    return roles.some((r) => String(r) === String(role));
  }

  public isLibrarian(): boolean {
    return this.hasRole(Role.ROLE_LIBRARIAN) || this.hasRole(Role.ROLE_ADMIN);
  }

  login(credentials: {
    usernameOrEmail: string;
    password: string;
  }): Observable<BackendAuthResponse> {
    return this.http.post<any>(`${this.API_URL}/login`, credentials).pipe(
      switchMap((response) => {
        const branchData = response.employeeOfBranch;
        if (branchData && typeof branchData === 'number') {
          return this.branchService.loadBranches().pipe(
            map(() => {
              const fullBranch = this.branchService.getBranchFromStore(branchData);
              return { ...response, employeeOfBranch: fullBranch || null };
            })
          );
        }
        return of(response);
      }),
      tap((response: BackendAuthResponse) => {
        this.authState.set({
          user: response.username,
          roles: response.roles,
          initialized: true,
          employeeOfBranch: response.employeeOfBranch,
        });
        void this.router.navigate(['/profil']);
      })
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
        this.authState.set({ user: null, roles: null, initialized: true, employeeOfBranch: null });
        void this.router.navigate(['/zaloguj-sie']);
      },
      error: () => {
        this.onLogoutCallbacks.forEach((cb) => cb());
        this.authState.set({ user: null, roles: null, initialized: true, employeeOfBranch: null });
        void this.router.navigate(['/zaloguj-sie']);
      },
    });
  }
}
