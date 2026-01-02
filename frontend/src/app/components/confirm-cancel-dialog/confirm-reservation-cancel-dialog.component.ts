import { Component, inject, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ReservationService } from '../../services/reservation.service';
import { finalize } from 'rxjs';
export interface ConfirmCancelData {
  reservationId: number;
  bookTitle?: string;
}
@Component({
  selector: 'app-confirm-cancel-dialog',
  standalone: true,
  templateUrl: './confirm-reservation-cancel-dialog.component.html',
})
export class ConfirmReservationCancelDialog {
  private dialogRef = inject(MatDialogRef<ConfirmReservationCancelDialog>);
  private reservationService = inject(ReservationService);
  public data: ConfirmCancelData = inject(MAT_DIALOG_DATA);

  loading = signal(false);
  error = signal<string | null>(null);
  success = signal(false);

  onNoClick(): void {
    this.dialogRef.close(this.success());
  }

  onConfirmClick(): void {
    if (this.loading()) return;

    this.loading.set(true);
    this.error.set(null);

    this.reservationService
      .cancelReservation(this.data.reservationId)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => {
          this.success.set(true);
        },
        error: (err) => {
          console.error('Błąd podczas anulowania rezerwacji:', err);

          let errorMessage = 'Nie udało się anulować rezerwacji. Spróbuj ponownie później.';
          if (err.error?.message) {
            errorMessage = err.error.message;
          }
          this.error.set(errorMessage);
        },
      });
  }
}
