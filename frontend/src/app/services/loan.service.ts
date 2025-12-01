import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SingleBook } from '../types';
import { environment } from '../../environments/environment';

export interface RentRequest {
  libraryItemId: number;
  userId: number;
}

@Injectable({
  providedIn: 'root',
})
export class LoanService {
  private http = inject(HttpClient);
  private API_URL = environment.apiUrl + 'rentals';

  /**
   * Get all items rented by a specific user
   */
  getUserLoans(userId: number): Observable<SingleBook[]> {
    return this.http.get<SingleBook[]>(`${this.API_URL}/user/${userId}`);
  }

  /**
   * Get all available items for rental
   */
  getAvailableItems(): Observable<SingleBook[]> {
    return this.http.get<SingleBook[]>(`${this.API_URL}/available`);
  }

  /**
   * Get all currently rented items (librarian view)
   */
  getAllRentedItems(): Observable<SingleBook[]> {
    return this.http.get<SingleBook[]>(`${this.API_URL}/all-rented`);
  }

  /**
   * Rent an item to a user
   */
  rentItem(request: RentRequest): Observable<SingleBook> {
    return this.http.post<SingleBook>(`${this.API_URL}/rent`, request);
  }

  /**
   * Return a rented item
   */
  returnItem(itemId: number): Observable<SingleBook> {
    return this.http.post<SingleBook>(`${this.API_URL}/return/${itemId}`, {});
  }

  /**
   * Extend loan by specified days (default 7)
   */
  extendLoan(itemId: number, days: number = 7): Observable<SingleBook> {
    return this.http.post<SingleBook>(`${this.API_URL}/${itemId}/extend?days=${days}`, {});
  }
}
