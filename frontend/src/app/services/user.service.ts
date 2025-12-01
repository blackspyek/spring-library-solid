import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../types';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);
  private API_URL = environment.apiUrl + 'users';

  /**
   * Find user by username
   */
  findByUsername(username: string): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/${username}`);
  }

  /**
   * Find user by email
   */
  findByEmail(email: string): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/email/${email}`);
  }

  /**
   * Find user by ID
   */
  findById(id: number): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/id/${id}`);
  }

  /**
   * Search users by query (username or email)
   */
  searchUsers(query: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.API_URL}/search?query=${encodeURIComponent(query)}`);
  }
}
