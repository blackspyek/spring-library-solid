import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

/**
 * Request to create a new reservation.
 * Uses itemId (not libraryItemId) to match microservices API.
 */
export interface ReservationRequest {
  itemId: number;
  branchId: number;
}

/**
 * Item details included in reservation response.
 */
export interface ReservationItem {
  id: number;
  title: string;
  imageUrl?: string;
  author?: string;
  description?: string;
}

/**
 * Reservation history from the API.
 * Note: branchId is returned instead of full branch object.
 * Use BranchService.getBranchFromStore(branchId) to get branch details.
 */
export interface ReservationHistory {
  id: number;
  item: ReservationItem;
  branchId: number;
  userId: number;
  reservedAt: string;
  expiresAt: string;
  resolvedAt: string | null;
  status: 'ACTIVE' | 'FULFILLED' | 'CANCELLED' | 'EXPIRED';
}

@Injectable({
  providedIn: 'root',
})
export class ReservationService {
  private http = inject(HttpClient);
  private API_URL = environment.apiUrl + 'reservations';

  /**
   * Create a new reservation.
   * UserId is extracted from JWT token on the backend.
   */
  createReservation(request: ReservationRequest): Observable<ReservationHistory> {
    return this.http.post<ReservationHistory>(this.API_URL, request);
  }

  /**
   * Cancel a reservation by its ID.
   * User can only cancel their own reservations (enforced by backend).
   */
  cancelReservation(reservationId: number): Observable<ReservationHistory> {
    return this.http.delete<ReservationHistory>(`${this.API_URL}/${reservationId}`);
  }

  /**
   * Get current user's active reservations.
   * UserId is extracted from JWT token on the backend.
   */
  getMyReservations(): Observable<ReservationHistory[]> {
    return this.http.get<ReservationHistory[]>(`${this.API_URL}/my`);
  }

  /**
   * Get count of current user's active reservations.
   */
  getReservationCount(): Observable<number> {
    return this.http.get<number>(`${this.API_URL}/count`);
  }
}
