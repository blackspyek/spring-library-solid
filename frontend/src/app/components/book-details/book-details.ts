import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import {SingleBook} from '../../types';

@Component({
  selector: 'app-book-details',
  standalone: true,
  imports: [CommonModule, MatDialogModule],
  templateUrl: './book-details.html',
})
export class BookDetailsComponent {
  constructor(
    public dialogRef: MatDialogRef<BookDetailsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SingleBook
  ) {}

  close(): void {
    this.dialogRef.close();
  }

  getStatusLabel(status: string): string {
    const labels: { [key: string]: string } = {
      AVAILABLE: 'Dostępny',
      RENTED: 'Wypożyczony',
      MAINTENANCE: 'W konserwacji',
      LOST: 'Zgubiony',
    };
    return labels[status] || status;
  }
}
