import { Component, OnInit, Inject, inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormBuilder } from '@angular/forms';
import { ProfileBookItemComponent } from '../profile-book-item/profile-book-item';
import { SingleBook } from '../../types';
import { RentalService } from '../../services/rental-service';
import { UserService } from '../../services/user-service';
import { filter, switchMap, take } from 'rxjs';

export interface DialogData {
  type: 'rent' | 'reservation';
}

export type DialogBookItem = SingleBook;

@Component({
  selector: 'app-active-books-dialog',
  standalone: true,
  imports: [CommonModule, MatButtonModule, ReactiveFormsModule, ProfileBookItemComponent],
  templateUrl: './active-books-dialog.html',
  styleUrl: './active-books-dialog.scss',
})
export class ActiveBooksDialog implements OnInit {
  private rentalService = inject(RentalService);
  private userService = inject(UserService);

  public dialogType: 'rent' | 'reservation';

  public activeItems: DialogBookItem[] = [];

  public loading = false;

  constructor(
    public dialogRef: MatDialogRef<ActiveBooksDialog>,
    private fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
  ) {
    this.dialogType = data.type;
  }

  ngOnInit(): void {
    if (this.dialogType === 'rent') {
      this.fetchRentedItems();
    } else {
      // temporary
      this.activeItems = this.getMockReservationItems();
    }
  }

  private fetchRentedItems(): void {
    this.loading = true;

    this.userService
      .getUserId()
      .pipe(
        filter((userId): userId is number => userId !== undefined && userId > 0),
        switchMap((userId) => this.rentalService.getRentedItems(userId)),
        take(1),
      )
      .subscribe({
        next: (items) => {
          this.activeItems = items;
          this.loading = false;
        },
        error: (err) => {
          console.error('Błąd pobierania wypożyczeń dla dialogu:', err);
          this.loading = false;
          this.activeItems = [];
        },
      });
  }

  // temporary
  private getMockReservationItems(): DialogBookItem[] {
    return [
      {
        id: 201,
        title: 'Księga Dżungli',
        author: 'Rudyard Kipling',
        dueDate: '2026-01-15T00:00:00',
        imageUrl: 'https://covers.openlibrary.org/b/id/9783084-L.jpg',
        status: 'RESERVED',
        description: 'Klasyka literatury dziecięcej.',
        rentedAt: '',
        pageCount: 250,
        isbn: '978-0140439070',
        paperType: 'B5',
        publisher: 'Puffin',
        shelfNumber: 5,
        genre: 'Literatura',
      },
      {
        id: 201,
        title: 'Księga Dżungli',
        author: 'Rudyard Kipling',
        dueDate: '2026-01-15T00:00:00',
        imageUrl: 'https://covers.openlibrary.org/b/id/9783084-L.jpg',
        status: 'RESERVED',
        description: 'Klasyka literatury dziecięcej.',
        rentedAt: '',
        pageCount: 250,
        isbn: '978-0140439070',
        paperType: 'B5',
        publisher: 'Puffin',
        shelfNumber: 5,
        genre: 'Literatura',
      },
      {
        id: 201,
        title: 'Księga Dżungli',
        author: 'Rudyard Kipling',
        dueDate: '2026-01-15T00:00:00',
        imageUrl: 'https://covers.openlibrary.org/b/id/9783084-L.jpg',
        status: 'RESERVED',
        description: 'Klasyka literatury dziecięcej.',
        rentedAt: '',
        pageCount: 250,
        isbn: '978-0140439070',
        paperType: 'B5',
        publisher: 'Puffin',
        shelfNumber: 5,
        genre: 'Literatura',
      },
      {
        id: 201,
        title: 'Księga Dżungli',
        author: 'Rudyard Kipling',
        dueDate: '2026-01-15T00:00:00',
        imageUrl: 'https://covers.openlibrary.org/b/id/9783084-L.jpg',
        status: 'RESERVED',
        description: 'Klasyka literatury dziecięcej.',
        rentedAt: '',
        pageCount: 250,
        isbn: '978-0140439070',
        paperType: 'B5',
        publisher: 'Puffin',
        shelfNumber: 5,
        genre: 'Literatura',
      },
    ] as DialogBookItem[];
  }

  onNoClick(): void {
    this.dialogRef.close(null);
  }
}
