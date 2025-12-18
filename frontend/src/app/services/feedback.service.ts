import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { FeedbackRequest, FeedbackResponse } from '../types';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class FeedbackService {
  private http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + 'feedback';

  /**
   * Submit feedback to the backend.
   */
  submitFeedback(request: FeedbackRequest): Observable<FeedbackResponse> {
    return this.http.post<FeedbackResponse>(this.apiUrl, request).pipe(
      catchError((error: HttpErrorResponse) => this.handleError(error))
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let message = 'Wystąpił nieoczekiwany błąd. Spróbuj ponownie później.';

    if (error.status === 429) {
      message = 'Przekroczono limit zgłoszeń. Spróbuj ponownie za godzinę.';
    } else if (error.status === 400) {
      message = error.error?.message || 'Nieprawidłowe dane formularza.';
    } else if (error.status === 0) {
      message = 'Brak połączenia z serwerem. Sprawdź połączenie internetowe.';
    }

    return throwError(() => ({ status: error.status, message }));
  }
}
