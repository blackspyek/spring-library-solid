import { Component, OnInit, inject, computed, signal } from '@angular/core';

import { LoanService } from '../../services/loan.service';
import { ExtendDialogResponse, ProfileBookItem, SingleBook } from '../../types';
import { ProfileBookItemComponent } from '../profile-book-item/profile-book-item';
import { ActiveBooksDialog } from '../active-books-dialog/active-books-dialog';
import { MatDialog } from '@angular/material/dialog';
import { filter, map, switchMap, take } from 'rxjs';
import { UserService } from '../../services/user.service';
import {
  ExtendRentalDialog,
  ExtendRentalDialogData,
} from '../extend-rental-dialog/extend-rental-dialog';

@Component({
  selector: 'app-user-rentals-container',
  standalone: true,
  imports: [ProfileBookItemComponent],
  templateUrl: './user-rentals-container.html',
  styleUrl: './user-rentals-container.scss',
})
export class UserRentalsContainer implements OnInit {
  dialog = inject(MatDialog);

  private loanService = inject(LoanService);
  private userService = inject(UserService);

  rentals = signal<ProfileBookItem[]>([]);
  loading = signal(true);

  itemsToDisplay = computed(() => this.rentals().slice(0, 2));

  ngOnInit() {
    this.loadRentedItems();
  }

  private loadRentedItems(): void {
    this.loading.set(true);

    this.userService
      .getUserId()
      .pipe(
        filter((userId): userId is number => !!userId && userId > 0),
        switchMap((userId) => this.loanService.getUserLoans(userId)),
        map((items: SingleBook[]) => items.map((item) => this.mapLoanToProfileItem(item))),
        take(1)
      )
      .subscribe({
        next: (mappedItems) => {
          this.rentals.set(mappedItems);
          this.loading.set(false);
        },
        error: (err) => {
          console.error('Błąd podczas pobierania wypożyczeń:', err);
          this.loading.set(false);
        },
      });
  }

  mapLoanToProfileItem(loan: SingleBook): ProfileBookItem {
    return {
      id: loan.id, // Same as itemId
      branchId: loan.rentedFromBranchId || 0,
      statusType: 'rent',
      rentalDueDate: loan.dueDate,
      isRentExtended: loan.rentExtended,

      item: {
        itemId: loan.id,
        title: loan.title,
        author: loan.author,
        imageUrl: loan.imageUrl,
        coverText: loan.title,
      },
    };
  }

  onRentalExtended(rental: ExtendDialogResponse): void {
    this.openExtendRentalDialog(rental);
  }

  private openExtendRentalDialog(rental: ExtendDialogResponse): void {
    const item = this.rentals().find((r) => r.id === rental.itemId);
    if (!item) {
      console.error('Cannot extend rental: item not found');
      return;
    }

    if (!item.branchId) {
      console.error('Cannot extend rental: branchId is missing');
      return;
    }

    const dialogData: ExtendRentalDialogData = {
      itemId: item.id,
      bookTitle: item.item.title,
      branchId: item.branchId,
    };

    const dialogRef = this.dialog.open(ExtendRentalDialog, {
      width: '500px',
      panelClass: 'extend-rental-dialog',
      autoFocus: true,
      data: dialogData,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        this.loadRentedItems();
      }
    });
  }

  openBooksDialog(): void {
    const dialogRef = this.dialog.open(ActiveBooksDialog, {
      width: '600px',
      panelClass: 'user-rentals-dialog',
      autoFocus: true,
      data: {
        type: 'rent',
        data: this.rentals(),
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        this.loadRentedItems();
      }
    });
  }
}
