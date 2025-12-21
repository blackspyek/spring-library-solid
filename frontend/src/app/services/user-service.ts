import { inject, Injectable } from '@angular/core';
import { catchError, map, Observable, of, shareReplay, throwError } from 'rxjs';
import { ApiTextResponse, UserProfile } from '../types';
import { AuthService } from './auth-service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { LibraryBranch, User } from '../types';
import { environment } from '../../environments/environment';

export interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
}

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private API_URL = environment.apiUrl + 'user';

  private http = inject(HttpClient);
  private authService = inject(AuthService);

  private currentProfileCache$: Observable<UserProfile> | null = null;
  private cachedUsername: string | null = null;

  constructor() {
    this.authService.registerLogoutCallback(() => this.clearProfileCache());
  }

  private replaceEmptyUserData(profile: UserProfile): UserProfile {
    const cleaned: UserProfile = { ...profile };

    if (!cleaned.email) cleaned.email = '-';
    if (!cleaned.phone) cleaned.phone = '-';
    if (!cleaned.name) cleaned.name = '-';
    if (!cleaned.surname) cleaned.surname = '-';
    if (!cleaned.readerId) cleaned.readerId = '-';
    if (!cleaned.qrCode) cleaned.qrCode = 'Błąd';

    return cleaned;
  }

  getCurrentUserProfile(): Observable<UserProfile> {
    const username = this.authService.currentUser();

    if (!username) {
      return of(
        this.replaceEmptyUserData({
          id: 0,
          email: '',
          phone: '',
          name: 'Niezalogowany',
          surname: 'Użytkownik',
          readerId: '',
          qrCode: '',
        }),
      );
    }

    if (this.currentProfileCache$ && this.cachedUsername === username) {
      return this.currentProfileCache$;
    }

    const profileUrl = `${this.API_URL}/username/${username}`;

    this.cachedUsername = username;
    this.currentProfileCache$ = this.http.get<UserProfile>(profileUrl).pipe(
      map((profile) => this.replaceEmptyUserData(profile)),
      catchError((error) => {
        console.error(`Błąd pobierania profilu dla ${username} z ${profileUrl}:`, error);
        this.currentProfileCache$ = null;

        return of(
          this.replaceEmptyUserData({
            id: 0,
            email: 'error@server.pl',
            phone: '',
            name: 'Błąd Serwera',
            surname: '',
            readerId: '',
            qrCode: '',
          }),
        );
      }),
      shareReplay(1),
    );

    return this.currentProfileCache$;
  }

  clearProfileCache(): void {
    this.currentProfileCache$ = null;
    this.cachedUsername = null;
  }

  updateFavouriteBranch(branchId: number | null): Observable<User> {
    let params = new HttpParams();
    if (branchId !== null) {
      params = params.set('branchId', branchId.toString());
    }
    return this.http.put<User>(`${this.API_URL}/favourite-branch`, null, { params });
  }

  getFavouriteBranch(): Observable<LibraryBranch> {
    return this.http.get<LibraryBranch>(`${this.API_URL}/favourite-branch`);
  }

  changePassword(request: ChangePasswordRequest): Observable<ApiTextResponse> {
    const passwordUrl = `${this.API_URL}/password`;

    return this.http.put<ApiTextResponse>(passwordUrl, request).pipe(
      catchError((error) => {
        console.error('Błąd zmiany hasła:', error);
        return throwError(() => error);
      }),
    );
  }

  getUserId(): Observable<number | undefined> {
    return this.getCurrentUserProfile().pipe(map((profile) => profile.id));
  }
}
