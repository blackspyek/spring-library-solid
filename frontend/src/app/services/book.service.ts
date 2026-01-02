import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SingleBook, PageResponse, BookAvailability } from '../types';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class BookService {
  private http = inject(HttpClient);

  private apiUrl = environment.apiUrl + 'items/book';

  getBooksPaginated(page: number, size = 16): Observable<PageResponse<SingleBook>> {
    return this.http.get<PageResponse<SingleBook>>(
      `${this.apiUrl}/pagination?page=${page}&size=${size}`,
    );
  }

  getTopGenres(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/genres`);
  }

  getOtherGenres(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/genres/other`);
  }

  getAllPublishers(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/publishers`);
  }

  getAllStatuses(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/statuses`);
  }

  searchBooks(
    query?: string,
    status?: string,
    publisher?: string,
    genres?: string[],
    page = 0,
    size = 16,
    sortBy?: string,
  ): Observable<PageResponse<SingleBook>> {
    let params = new HttpParams().set('page', page.toString()).set('size', size.toString());

    if (query && query.trim()) {
      params = params.set('query', query.trim());
    }
    if (status) {
      params = params.set('status', status);
    }
    if (publisher && publisher.trim()) {
      params = params.set('publisher', publisher.trim());
    }
    if (genres && genres.length > 0) {
      genres.forEach((genre) => {
        params = params.append('genres', genre);
      });
    }
    if (sortBy) {
      params = params.set('sort', sortBy);
    }

    return this.http.get<PageResponse<SingleBook>>(`${this.apiUrl}/search`, { params });
  }

  getRecentBooks(limit = 7): Observable<SingleBook[]> {
    return this.http.get<SingleBook[]>(`${this.apiUrl}/recent?limit=${limit}`);
  }

  getPopularBooks(limit = 10): Observable<SingleBook[]> {
    return this.http.get<SingleBook[]>(`${this.apiUrl}/popular?limit=${limit}`);
  }

  getBookAvailability(bookId: number): Observable<BookAvailability> {
    return this.http.get<BookAvailability>(`${this.apiUrl}/${bookId}/availability`);
  }
}
