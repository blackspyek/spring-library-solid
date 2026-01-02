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
  mustChangePassword: boolean;
}
interface AuthState {
  user: string | null;
  roles: Role[] | null;
  initialized: boolean;
  employeeOfBranch: LibraryBranch | null;
  mustChangePassword: boolean;
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
    mustChangePassword: false,
  });

  public currentUser = computed(() => this.authState().user);
  public isLoggedIn = computed(() => !!this.authState().user);
  public isInitialized = computed(() => this.authState().initialized);
  public employeeOfBranch = computed(() => this.authState().employeeOfBranch);
  public mustChangePassword = computed(() => this.authState().mustChangePassword);

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
            }),
          );
        }
        // If it looks like an object or is null, return as is
        return of(response);
      }),
      tap((response: BackendAuthResponse) => {
        const mustChange = (response as any).mustChangePassword || false;
        this.authState.set({
          user: response.username,
          roles: response.roles,
          initialized: true,
          employeeOfBranch: response.employeeOfBranch,
          mustChangePassword: mustChange,
        });
        // Redirect to password change page if user must change password
        if (mustChange) {
          void this.router.navigate(['/zmiana-hasla']);
        }
      }),
      catchError(() => {
        this.authState.set({
          user: null,
          roles: null,
          initialized: true,
          employeeOfBranch: null,
          mustChangePassword: false,
        });
        return of(null);
      }),
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
        console.log('=== DEBUG raw response from /login ===');
        console.log('Raw response:', JSON.stringify(response));
        console.log('Raw mustChangePassword:', response.mustChangePassword);

        const branchData = response.employeeOfBranch;
        if (branchData && typeof branchData === 'number') {
          return this.branchService.loadBranches().pipe(
            map(() => {
              const fullBranch = this.branchService.getBranchFromStore(branchData);
              return {
                ...response,
                employeeOfBranch: fullBranch || null,
                mustChangePassword: response.mustChangePassword,
              };
            }),
          );
        }
        return of(response);
      }),
      tap((response: BackendAuthResponse) => {
        const mustChange = response.mustChangePassword || false;
        console.log('=== DEBUG login() response ===');
        console.log('mustChangePassword from response:', response.mustChangePassword);
        console.log('mustChange value:', mustChange);

        this.authState.set({
          user: response.username,
          roles: response.roles,
          initialized: true,
          employeeOfBranch: response.employeeOfBranch,
          mustChangePassword: mustChange,
        });
        // If user must change password, redirect to password change page
        if (mustChange) {
          console.log('Redirecting to /zmiana-hasla');
          void this.router.navigate(['/zmiana-hasla']);
        } else {
          console.log('Redirecting to /profil');
          void this.router.navigate(['/profil']);
        }
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
        this.authState.set({
          user: null,
          roles: null,
          initialized: true,
          employeeOfBranch: null,
          mustChangePassword: false,
        });
        void this.router.navigate(['/zaloguj-sie']);
      },
      error: () => {
        this.onLogoutCallbacks.forEach((cb) => cb());
        this.authState.set({
          user: null,
          roles: null,
          initialized: true,
          employeeOfBranch: null,
          mustChangePassword: false,
        });
        void this.router.navigate(['/zaloguj-sie']);
      },
    });
  }

  /**
   * Clear mustChangePassword flag after user successfully changes password
   */
  clearMustChangePassword(): void {
    this.authState.update((state) => ({
      ...state,
      mustChangePassword: false,
    }));
  }

  /**
   * Reset password by email and PESEL
   */
  resetPassword(request: {
    email: string;
    pesel: string;
  }): Observable<{ success: boolean; message: string }> {
    return this.http.post<{ success: boolean; message: string }>(
      `${this.API_URL}/reset-password`,
      request,
    );
  }
}
