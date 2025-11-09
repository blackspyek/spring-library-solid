import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, Observable, of, tap } from 'rxjs';
import { AuthResponse, Role, User } from '../types';
interface AuthState {
  user: User | null;
  token: string | null;
}
const dummy_user: User = {
  id: 1,
  email: 'test@o2.pl',
  first_name: 'John',
  last_name: 'Doe',
  phone_number: '123456789',
  roles: [Role.ROLE_READER],
};
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private API_URL = 'SECRET_API_URL';

  private authState = signal<AuthState>({
    user: null,
    token: null,
  });

  public currentUser = computed(() => this.authState().user);
  public isLoggedIn = computed(() => !!this.authState().user);

  login(credentials: { email: string; password: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, credentials).pipe(
      tap((response) => {
        this.authState.set({ user: response.user, token: response.token });

        void this.router.navigate(['/dashboard']);
      }),
      catchError((error) => {
        console.error('Błąd logowania:', error);
        return of(error);
      }),
    );
  }
}
