import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import {SingleBook, PageResponse} from '../types';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  private apiUrl = environment.apiUrl + 'book';

  constructor(private http: HttpClient) {}

  getBooksPaginated(page: number, size: number = 16): Observable<PageResponse<SingleBook>> {
    return this.http.get<PageResponse<SingleBook>>(`${this.apiUrl}/pagination?page=${page}&size=${size}`);
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
    page: number = 0,
    size: number = 16,
    sortBy?: string
  ): Observable<PageResponse<SingleBook>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

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
      genres.forEach(genre => {
        params = params.append('genres', genre);
      });
    }
    if (sortBy) {
      params = params.set('sort', sortBy);
    }

    return this.http.get<PageResponse<SingleBook>>(`${this.apiUrl}/search`, { params });
  }
}
