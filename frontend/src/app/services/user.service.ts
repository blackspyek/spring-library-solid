import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { catchError, map, Observable, of, shareReplay, throwError } from 'rxjs';
import { Role, User, LibraryBranch, UserProfile, ApiTextResponse } from '../types';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

export interface CreateUserRequest {
  email: string;
  firstName: string;
  lastName: string;
  pesel: string;
  phone: string;
  address: {
    street: string;
    city: string;
    postalCode: string;
    country: string;
    buildingNumber: string;
    apartmentNumber?: string;
  };
}

export interface RoleSetDto {
  roles: Role[];
}

export interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
}

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);

  private readonly API_URL = `${environment.apiUrl}users`;
  private readonly AUTH_URL = `${environment.apiUrl}auth`;

  private currentProfileCache$: Observable<UserProfile> | null = null;
  private cachedUsername: string | null = null;

  constructor() {
    this.authService.registerLogoutCallback(() => this.clearProfileCache());
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
        console.error(`Błąd pobierania profilu dla ${username}:`, error);
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

  getUserId(): Observable<number | undefined> {
    return this.getCurrentUserProfile().pipe(map((profile) => profile.id));
  }

  clearProfileCache(): void {
    this.currentProfileCache$ = null;
    this.cachedUsername = null;
  }

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.API_URL);
  }

  searchUsers(query: string): Observable<User[]> {
    if (!query) return this.getAllUsers();
    return this.getAllUsers();
  }

  findById(id: number): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/id/${id}`);
  }

  findByUsername(username: string): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/username/${username}`);
  }

  findByEmail(email: string): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/email/${email}`);
  }

  createUser(data: CreateUserRequest): Observable<User> {
    return this.http.post<User>(`${this.AUTH_URL}/register`, data);
  }

  updateUserRole(username: string, newRole: Role): Observable<void> {
    const body: RoleSetDto = { roles: [newRole] };
    return this.http.put<void>(`${this.API_URL}/roles/${username}`, body);
  }

  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${userId}`);
  }

  changePassword(request: ChangePasswordRequest): Observable<ApiTextResponse> {
    return this.http.put<ApiTextResponse>(`${this.API_URL}/password`, request).pipe(
      catchError((error) => {
        console.error('Błąd zmiany hasła:', error);
        return throwError(() => error);
      }),
    );
  }

  getEmployeeBranch(): Observable<LibraryBranch | null> {
    return this.http
      .get<LibraryBranch>(`${this.API_URL}/employee-branch`, { withCredentials: true })
      .pipe(catchError(() => of(null)));
  }

  getFavouriteBranch(): Observable<LibraryBranch> {
    return this.http.get<LibraryBranch>(`${this.API_URL}/favourite-branch`);
  }

  updateFavouriteBranch(branchId: number | null): Observable<User> {
    let params = new HttpParams();
    if (branchId !== null) {
      params = params.set('branchId', branchId.toString());
    }
    return this.http.put<User>(`${this.API_URL}/favourite-branch`, null, { params });
  }

  private replaceEmptyUserData(profile: UserProfile): UserProfile {
    return {
      ...profile,
      email: profile.email || '-',
      phone: profile.phone || '-',
      name: profile.name || '-',
      surname: profile.surname || '-',
      readerId: profile.readerId || '-',
      qrCode: profile.qrCode || 'Błąd',
    };
  }
}
