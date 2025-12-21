import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable, of } from 'rxjs';
import { Role, User, LibraryBranch } from '../types';
import { environment } from '../../environments/environment';

export interface CreateUserRequest {
  email: string;
  password: string;
}

export interface RoleSetDto {
  roles: Role[];
}

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);
  private API_URL = environment.apiUrl + 'user';
  private AUTH_URL = environment.apiUrl + 'auth';

  findByUsername(username: string): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/username/${username}`);
  }

  findByEmail(email: string): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/email/${email}`);
  }

  findById(id: number): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/id/${id}`);
  }

  searchUsers(query: string): Observable<User[]> {
    if (!query) {
      return this.getAllUsers();
    }
    return this.getAllUsers();
  }

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.API_URL}`);
  }

  createUser(data: CreateUserRequest): Observable<User> {
    return this.http.post<User>(`${this.AUTH_URL}/register`, data);
  }

  updateUserRole(username: string, newRole: Role): Observable<void> {
    const body: RoleSetDto = {
      roles: [newRole]
    };
    return this.http.put<void>(`${this.API_URL}/roles/${username}`, body);
  }

  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${userId}`);
  }

  getEmployeeBranch(): Observable<LibraryBranch | null> {
    return this.http
      .get<LibraryBranch>(`${this.API_URL}/employee-branch`, { withCredentials: true })
      .pipe(catchError(() => of(null)));
  }
}
