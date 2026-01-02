import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, map, Observable, of, throwError } from 'rxjs';
import { SingleBook, RentRequest, RentalHistoryItem } from '../types';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class LoanService {
  private http = inject(HttpClient);
  private API_URL_CATALOG = environment.apiUrl + 'items';
  private API_URL_RENTALS = environment.apiUrl + 'rentals';

  getUserLoans(userId: number): Observable<SingleBook[]> {
    return this.http.get<SingleBook[]>(`${this.API_URL_CATALOG}/user/${userId}`);
  }

  getAvailableItems(): Observable<SingleBook[]> {
    return this.http.get<SingleBook[]>(`${this.API_URL_CATALOG}/available`);
  }

  getAvailableItemsByBranch(branchId: number): Observable<SingleBook[]> {
    return this.http.get<SingleBook[]>(`${this.API_URL_CATALOG}/branch/${branchId}/available`);
  }

  getAllRentedItems(): Observable<SingleBook[]> {
    return this.http.get<SingleBook[]>(`${this.API_URL_CATALOG}/rented`);
  }

  rentItem(itemId: number, userId: number, branchId: number): Observable<SingleBook> {
    const rentDto = {
      userId,
      libraryItemId: itemId,
      branchId,
    };

    return this.http.put<SingleBook>(`${this.API_URL_RENTALS}/rent`, rentDto);
  }

  returnItem(itemId: number, branchId: number): Observable<any> {
    return this.http.put<any>(
      `${this.API_URL_RENTALS}/${itemId}/return`,
      {},
      {
        params: { branchId: branchId.toString() },
      }
    );
  }

  extendLoan(itemId: number, branchId: number, days = 7): Observable<any> {
    return this.http.put<any>(
      `${this.API_URL_RENTALS}/${itemId}/extend`,
      {},
      {
        params: {
          branchId: branchId.toString(),
          days: days.toString(),
        },
      }
    );
  }

  getRecentHistory(limit = 3): Observable<RentalHistoryItem[]> {
    const url = `${this.API_URL_RENTALS}/history/recent?limit=${limit}`;

    return this.http.get<RentalHistoryItem[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('Błąd pobierania ostatnich wypożyczeń:', error);
        return of([]);
      })
    );
  }
  exportRentalHistory(): Observable<Blob> {
    const url = `${this.API_URL_RENTALS}/history/export`;

    return this.http
      .get(url, {
        responseType: 'blob',
      })
      .pipe(
        catchError((error: HttpErrorResponse) => {
          console.error('Błąd eksportu historii wypożyczeń:', error);
          return throwError(() => new Error('Nie udało się pobrać historii wypożyczeń'));
        })
      );
  }

  getRentedItems(userId: number): Observable<SingleBook[]> {
    const url = `${this.API_URL_RENTALS}/user/${userId}`;

    return this.http.get<SingleBook[]>(url).pipe(
      map((items) => items),
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          console.warn(`Użytkownik ${userId} nie ma aktualnie wypożyczonych książek.`);
          return of([]);
        }
        return throwError(() => new Error(`Błąd pobierania wypożyczeń: ${error.message}`));
      })
    );
  }
}
