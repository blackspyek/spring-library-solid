import { Component, OnInit, inject } from '@angular/core';

import { UserService } from '../../services/user-service';
import { SingleBook } from '../../types';
import { ProfileBookItemComponent } from '../profile-book-item/profile-book-item';
import { ActiveBooksDialog } from '../active-books-dialog/active-books-dialog';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-user-reservations-container',
  standalone: true,
  imports: [ProfileBookItemComponent],
  templateUrl: './user-reservations-container.html',
  styleUrl: './user-reservations-container.scss',
})
export class UserReservationsContainer implements OnInit {
  dialog = inject(MatDialog);

  //private reservationService = inject(ReservationService);
  private userService = inject(UserService);

  reservedItems: SingleBook[] = [];
  loading = true;

  itemsToDisplay: SingleBook[] = [];

  ngOnInit() {
    /*
    this.userService
      .getUserId()
      .pipe(
        filter((userId): userId is number => userId !== undefined && userId > 0),
        switchMap((userId) => {
          return this.reservationService.getRentedItems(userId);
        }),
        take(1),
      )
      .subscribe({
        next: (items) => {
          this.reservedItems = items;
          this.loading = false;
          this.itemsToDisplay = items.slice(0, 2);
        },
        error: (err) => {
          console.error('Błąd podczas pobierania wypożyczeń:', err);
          this.loading = false;
        },
      });

     */
    //temporary
    this.setMockReservedItems();
  }

  private setMockReservedItems(): void {
    const mockItems: SingleBook[] = [
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
    ];

    this.reservedItems = mockItems;
    this.loading = false;

    this.itemsToDisplay = mockItems.slice(0, 2);
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
