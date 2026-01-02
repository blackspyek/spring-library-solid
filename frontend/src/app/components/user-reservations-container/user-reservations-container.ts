import { Component, OnInit, inject, signal, computed } from '@angular/core';

import { ReservationService, ReservationHistory } from '../../services/reservation.service';
import { BranchService } from '../../services/branch.service';
import {
  ActiveBooksDialog,
  ActiveBooksDialogData,
} from '../active-books-dialog/active-books-dialog';
import { MatDialog } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { LibraryBranch, ProfileBookItem } from '../../types';
import { map } from 'rxjs';
import { ConfirmReservationCancelDialog } from '../confirm-cancel-dialog/confirm-reservation-cancel-dialog.component';

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
  private branchService = inject(BranchService);

  reservations = signal<ProfileBookItem[]>([]);
  loading = signal(true);

  itemsToDisplay = computed(() => this.reservations().slice(0, 2));

  ngOnInit() {
    // Load branches first (cached), then load reservations
    this.branchService.loadBranches().subscribe({
      next: () => this.loadReservations(),
      error: () => this.loadReservations(), // Continue even if branches fail
    });
  }

  loadReservations(): void {
    this.loading.set(true);
    this.reservationService
      .getMyReservations()
      .pipe(
        map((items: ReservationHistory[]) =>
          items.map((res) => this.mapReservationToProfileItem(res))
        )
      )
      .subscribe({
        next: (mappedItems: ProfileBookItem[]) => {
          this.reservations.set(mappedItems);
          this.loading.set(false);
        },
        error: (err) => {
          console.error('Błąd podczas pobierania rezerwacji:', err);
          this.loading.set(false);
        },
      });
  }

  mapReservationToProfileItem(res: ReservationHistory): ProfileBookItem {
    return {
      id: res.id,
      branchId: res.branchId,
      statusType: 'reservation',
      reservationExpiresAt: res.expiresAt,

      item: {
        title: res.item.title,
        author: res.item.author,
        imageUrl: res.item.imageUrl,
        coverText: res.item.title,
      },
    };
  }

  /**
   * Get branch details from the store by ID.
   */
  getBranch(branchId: number): LibraryBranch | undefined {
    return this.branchService.getBranchFromStore(branchId);
  }

  openCancelDialog(reservationId: number): void {
    const item = this.reservations().find((r) => r.id === reservationId);

    const dialogRef = this.dialog.open(ConfirmReservationCancelDialog, {
      width: '350px',
      data: {
        reservationId: reservationId,
        bookTitle: item?.item.title,
      },
    });

    dialogRef.afterClosed().subscribe((success) => {
      if (success) {
        this.reservations.set(this.reservations().filter((item) => item.id !== reservationId));
      }
    });
  }

  openBooksDialog(): void {
    this.dialog.open(ActiveBooksDialog, {
      width: '600px',
      panelClass: 'user-reservations-dialog',
      autoFocus: true,
      data: {
        type: 'reservation',
        data: this.reservations(),
        onCancelReservation: (reservationId: number) => {
          this.reservations.set(this.reservations().filter((item) => item.id !== reservationId));
        },
      } as ActiveBooksDialogData,
    });
  }
}
