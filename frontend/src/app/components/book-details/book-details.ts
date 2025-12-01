import { Component, inject } from '@angular/core';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { SingleBook } from '../../types';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-book-details',
  standalone: true,
  // Dodano NgOptimizedImage, aby obsłużyć [ngSrc] w HTML
  imports: [CommonModule, MatDialogModule, NgOptimizedImage, MatIconModule],
  templateUrl: './book-details.html',
})
export class BookDetailsComponent {
  // Nowoczesne wstrzykiwanie zależności (zamiast konstruktora)
  readonly dialogRef = inject(MatDialogRef<BookDetailsComponent>);
  readonly data = inject<SingleBook>(MAT_DIALOG_DATA);

  close(): void {
    this.dialogRef.close();
  }

  getStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      AVAILABLE: 'Dostępny',
      RENTED: 'Wypożyczony',
      MAINTENANCE: 'W konserwacji',
      LOST: 'Zgubiony',
    };
    return labels[status] || status;
  }
}
