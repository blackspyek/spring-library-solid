import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SingleBook } from '../types';
import { environment } from '../../environments/environment';

export interface RentRequest {
  libraryItemId: number;
  userId: number;
  branchId: number;
}

@Injectable({
  providedIn: 'root',
})
export class LoanService {
  private http = inject(HttpClient);
  private API_URL = environment.apiUrl + 'rentals';

  getUserLoans(userId: number): Observable<SingleBook[]> {
    return this.http.get<SingleBook[]>(`${this.API_URL}/user/${userId}`);
  }

  getAvailableItems(): Observable<SingleBook[]> {
    return this.http.get<SingleBook[]>(`${this.API_URL}/available`);
  }

  getAvailableItemsByBranch(branchId: number): Observable<SingleBook[]> {
    return this.http.get<SingleBook[]>(`${this.API_URL}/available/branch/${branchId}`);
  }

  getAllRentedItems(): Observable<SingleBook[]> {
    return this.http.get<SingleBook[]>(`${this.API_URL}/all-rented`);
  }

  rentItem(request: RentRequest): Observable<SingleBook> {
    return this.http.post<SingleBook>(`${this.API_URL}/rent`, request);
  }

  returnItem(itemId: number): Observable<SingleBook> {
    return this.http.post<SingleBook>(`${this.API_URL}/return/${itemId}`, {});
  }

  extendLoan(itemId: number, days: number = 7): Observable<SingleBook> {
    return this.http.post<SingleBook>(`${this.API_URL}/${itemId}/extend?days=${days}`, {});
  }
}
