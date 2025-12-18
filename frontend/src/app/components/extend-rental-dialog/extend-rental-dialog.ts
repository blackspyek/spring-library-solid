import { Component, inject, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { RentalService } from '../../services/rental-service';
import { finalize } from 'rxjs';

export interface ExtendRentalDialogData {
  itemId: number;
  bookTitle: string;
}

@Component({
  selector: 'app-extend-rental-dialog',
  standalone: true,
  imports: [MatButtonModule],
  templateUrl: './extend-rental-dialog.html',
  styleUrl: './extend-rental-dialog.scss',
})
export class ExtendRentalDialog {
  private dialogRef = inject(MatDialogRef<ExtendRentalDialog>);
  private rentalService = inject(RentalService);
  public data: ExtendRentalDialogData = inject(MAT_DIALOG_DATA);

  loading = signal(false);
  error = signal<string | null>(null);
  success = signal(false);

  onNoClick(): void {
    this.dialogRef.close(false);
  }

  onConfirmClick(): void {
    if (this.loading()) return;

    this.loading.set(true);
    this.error.set(null);

    this.rentalService
      .extendRental(this.data.itemId)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => {
          this.success.set(true);

        },
        error: (err) => {
          console.error('Błąd przedłużenia wypożyczenia:', err);

          let errorMessage = 'Nieoczekiwany błąd serwera. Spróbuj ponownie później.';

          if (err.error) {
            if (err.error.errors?.error && Array.isArray(err.error.errors.error)) {
              errorMessage = err.error.errors.error.join('. ');
            } else if (typeof err.error === 'string') {
              errorMessage = err.error;
            } else if (err.error.message && err.error.message !== 'An unexpected error occurred') {
              errorMessage = err.error.message;
            }
          }

          if (errorMessage.includes('already been extended')) {
            errorMessage = 'Błąd! Nie możesz przedłużyć wypożyczenia więcej niż raz.';
          } else if (errorMessage.includes('not currently rented')) {
            errorMessage = 'Książka nie jest aktualnie wypożyczona.';
          }

          this.error.set(errorMessage);
        },
      });
  }
}
