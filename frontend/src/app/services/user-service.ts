import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, map, Observable, of, throwError } from 'rxjs';
import { ApiTextResponse, UserProfile } from '../types';
import { AuthService } from './auth-service';
import { environment } from '../../environments/environment';

export interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
}

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = environment.apiUrl + 'user';

  private http = inject(HttpClient);
  private authService = inject(AuthService);

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

    const profileUrl = `${this.apiUrl}/username/${username}`;

    return this.http.get<UserProfile>(profileUrl).pipe(
      map((profile) => this.replaceEmptyUserData(profile)),
      catchError((error) => {
        console.error(`Błąd pobierania profilu dla ${username} z ${profileUrl}:`, error);

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
    );
  }

  changePassword(request: ChangePasswordRequest): Observable<ApiTextResponse> {
    const passwordUrl = `${this.apiUrl}/password`;

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
