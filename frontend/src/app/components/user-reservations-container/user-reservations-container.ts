import { Component, OnInit, inject, signal } from '@angular/core';

import { ReservationService, ReservationHistory } from '../../services/reservation.service';
import { ProfileBookItemComponent } from '../profile-book-item/profile-book-item';
import { ActiveBooksDialog } from '../active-books-dialog/active-books-dialog';
import { MatDialog } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-reservations-container',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-reservations-container.html',
  styleUrl: './user-reservations-container.scss',
})
export class UserReservationsContainer implements OnInit {
  dialog = inject(MatDialog);
  private reservationService = inject(ReservationService);

  reservations = signal<ReservationHistory[]>([]);
  loading = signal(true);
  cancellingItemId = signal<number | null>(null);

  get itemsToDisplay(): ReservationHistory[] {
    return this.reservations().slice(0, 2);
  }

  ngOnInit() {
    this.loadReservations();
  }

  loadReservations(): void {
    this.loading.set(true);
    this.reservationService.getMyReservations().subscribe({
      next: (items) => {
        this.reservations.set(items);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Błąd podczas pobierania rezerwacji:', err);
        this.loading.set(false);
      },
    });
  }

  requestCancel(itemId: number): void {
    this.cancellingItemId.set(itemId);
  }

  confirmCancel(): void {
    const itemId = this.cancellingItemId();
    if (!itemId) return;

    this.reservationService.cancelReservation(itemId).subscribe({
      next: () => {
        this.cancellingItemId.set(null);
        this.loadReservations();
      },
      error: (err) => {
        console.error('Błąd podczas anulowania rezerwacji:', err);
        this.cancellingItemId.set(null);
      },
    });
  }

  cancelConfirmation(): void {
    this.cancellingItemId.set(null);
  }

  openBooksDialog(): void {
    this.dialog.open(ActiveBooksDialog, {
      width: '600px',
      panelClass: 'user-reservations-dialog',
      autoFocus: true,
      data: {
        type: 'reservation',
      },
    });
  }
}
