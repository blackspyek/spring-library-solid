import { Component, OnInit, inject } from '@angular/core';

import { RentalService } from '../../services/rental-service';
import { UserService } from '../../services/user-service';
import { SingleBook } from '../../types';
import { ProfileBookItemComponent } from '../profile-book-item/profile-book-item';
import { ActiveBooksDialog } from '../active-books-dialog/active-books-dialog';
import { MatDialog } from '@angular/material/dialog';
import { filter, switchMap, take } from 'rxjs';

@Component({
  selector: 'app-user-rentals-container',
  standalone: true,
  imports: [ProfileBookItemComponent],
  templateUrl: './user-rentals-container.html',
  styleUrl: './user-rentals-container.scss',
})
export class UserRentalsContainer implements OnInit {
  dialog = inject(MatDialog);

  private rentalService = inject(RentalService);
  private userService = inject(UserService);

  rentedItems: SingleBook[] = [];
  loading = true;

  itemsToDisplay: SingleBook[] = [];

  ngOnInit() {
    this.loadRentedItems();
  }

  private loadRentedItems(): void {
    this.userService
      .getUserId()
      .pipe(
        filter((userId): userId is number => userId !== undefined && userId > 0),
        switchMap((userId) => {
          return this.rentalService.getRentedItems(userId);
        }),
        take(1)
      )
      .subscribe({
        next: (items) => {
          this.rentedItems = items;
          this.loading = false;
          this.itemsToDisplay = items.slice(0, 2);
        },
        error: (err) => {
          console.error('Błąd podczas pobierania wypożyczeń:', err);
          this.loading = false;
        },
      });
  }

  onRentalExtended(itemId: number): void {
    this.loadRentedItems();
  }

  openBooksDialog(): void {
    const dialogRef = this.dialog.open(ActiveBooksDialog, {
      width: '600px',
      panelClass: 'user-rentals-dialog',
      autoFocus: true,
      data: {
        type: 'rent',
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        this.loadRentedItems();
      }
    });
  }
}
