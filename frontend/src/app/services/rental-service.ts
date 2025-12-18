import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, map, of, throwError } from 'rxjs';
import { SingleBook, RentalHistoryItem } from '../types';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class RentalService {
  private http = inject(HttpClient);

  private apiUrl = environment.apiUrl + 'rentals';

  getRentedItems(userId: number): Observable<SingleBook[]> {
    const url = `${this.apiUrl}/user/${userId}`;

    return this.http.get<SingleBook[]>(url).pipe(
      map((items) => items),
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          console.warn(`Użytkownik ${userId} nie ma aktualnie wypożyczonych książek.`);
          return of([]);
        }
        return throwError(() => new Error(`Błąd pobierania wypożyczeń: ${error.message}`));
      }),
    );
  }

  getRecentHistory(limit: number = 3): Observable<RentalHistoryItem[]> {
    const url = `${this.apiUrl}/history/recent?limit=${limit}`;

    return this.http.get<RentalHistoryItem[]>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('Błąd pobierania ostatnich wypożyczeń:', error);
        return of([]);
      }),
    );
  }

  exportRentalHistory(): Observable<Blob> {
    const url = `${this.apiUrl}/history/export`;

    return this.http
      .get(url, {
        responseType: 'blob',
      })
      .pipe(
        catchError((error: HttpErrorResponse) => {
          console.error('Błąd eksportu historii wypożyczeń:', error);
          return throwError(() => new Error('Nie udało się pobrać historii wypożyczeń'));
        }),
      );
  }
}
