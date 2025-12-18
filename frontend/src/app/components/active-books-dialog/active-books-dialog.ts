import { Component, OnInit, inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { ReactiveFormsModule } from '@angular/forms';
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
  imports: [MatButtonModule, ReactiveFormsModule, ProfileBookItemComponent],
  templateUrl: './active-books-dialog.html',
  styleUrl: './active-books-dialog.scss',
})
export class ActiveBooksDialog implements OnInit {
  dialogRef = inject<MatDialogRef<ActiveBooksDialog>>(MatDialogRef);
  data = inject<DialogData>(MAT_DIALOG_DATA);

  private rentalService = inject(RentalService);
  private userService = inject(UserService);

  public dialogType: 'rent' | 'reservation';

  public activeItems: DialogBookItem[] = [];

  public loading = false;

  constructor() {
    const data = this.data;

    this.dialogType = data.type;
  }

  ngOnInit(): void {
    if (this.dialogType === 'rent') {
      this.fetchRentedItems();
    } else {
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
        take(1)
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
        id: 202,
        title: 'Mały Książę',
        author: 'Antoine de Saint-Exupéry',
        dueDate: '2026-01-20T00:00:00',
        imageUrl: 'https://covers.openlibrary.org/b/id/8228691-L.jpg',
        status: 'RESERVED',
        description: 'Opowieść o małym księciu.',
        rentedAt: '',
        pageCount: 96,
        isbn: '978-0156012195',
        paperType: 'A5',
        publisher: 'Mariner Books',
        shelfNumber: 3,
        genre: 'Literatura',
      },
      {
        id: 203,
        title: 'Hobbit',
        author: 'J.R.R. Tolkien',
        dueDate: '2026-01-25T00:00:00',
        imageUrl: 'https://covers.openlibrary.org/b/id/8406786-L.jpg',
        status: 'RESERVED',
        description: 'Przygody Bilbo Bagginsa.',
        rentedAt: '',
        pageCount: 310,
        isbn: '978-0547928227',
        paperType: 'B5',
        publisher: 'Houghton Mifflin',
        shelfNumber: 7,
        genre: 'Fantasy',
      },
      {
        id: 204,
        title: 'Władca Pierścieni',
        author: 'J.R.R. Tolkien',
        dueDate: '2026-01-30T00:00:00',
        imageUrl: 'https://covers.openlibrary.org/b/id/8406795-L.jpg',
        status: 'RESERVED',
        description: 'Epicka opowieść o Śródziemiu.',
        rentedAt: '',
        pageCount: 1200,
        isbn: '978-0618640157',
        paperType: 'B5',
        publisher: 'Houghton Mifflin',
        shelfNumber: 7,
        genre: 'Fantasy',
      },
    ] as DialogBookItem[];
  }

  onNoClick(): void {
    this.dialogRef.close(null);
  }

  onRentalExtended(itemId: number): void {
    this.fetchRentedItems();
    this.dialogRef.close(true);
  }
}
