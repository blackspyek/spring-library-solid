import { Component, inject } from '@angular/core';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { SingleBook } from '../../types';

@Component({
  selector: 'app-book-details',
  standalone: true,
  imports: [CommonModule, MatDialogModule, NgOptimizedImage],
  templateUrl: './book-details.html',
})
export class BookDetailsComponent {
  dialogRef = inject<MatDialogRef<BookDetailsComponent>>(MatDialogRef);
  data = inject<SingleBook>(MAT_DIALOG_DATA);

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
