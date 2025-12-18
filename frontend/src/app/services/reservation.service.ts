import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ReservationRequest {
  libraryItemId: number;
  branchId: number;
}

export interface ReservationHistory {
  id: number;
  item: {
    id: number;
    title: string;
    imageUrl: string;
  };
  branch: {
    id: number;
    branchNumber: string;
    address: string;
    city: string;
  };
  reservedAt: string;
  expiresAt: string;
  status: 'RUNNING' | 'RESOLVED' | 'CANCELLED' | 'EXPIRED';
}

@Injectable({
  providedIn: 'root',
})
export class ReservationService {
  private http = inject(HttpClient);
  private API_URL = environment.apiUrl + 'reservations';

  createReservation(request: ReservationRequest): Observable<ReservationHistory> {
    return this.http.post<ReservationHistory>(this.API_URL, request);
  }

  cancelReservation(itemId: number): Observable<unknown> {
    return this.http.delete(`${this.API_URL}/${itemId}`);
  }

  getMyReservations(): Observable<ReservationHistory[]> {
    return this.http.get<ReservationHistory[]>(`${this.API_URL}/my`);
  }

  getReservationCount(): Observable<number> {
    return this.http.get<number>(`${this.API_URL}/count`);
  }
}
