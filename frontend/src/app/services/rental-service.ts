import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, map, of, throwError } from 'rxjs';
import { SingleBook } from '../types';
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
      })
    );
  }
}
