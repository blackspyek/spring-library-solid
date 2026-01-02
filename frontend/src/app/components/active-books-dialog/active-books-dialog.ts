import { Component, OnInit, inject, signal } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { ReactiveFormsModule } from '@angular/forms';
import { ProfileBookItemComponent } from '../profile-book-item/profile-book-item';
import { ExtendDialogResponse, ProfileBookItem } from '../../types';

import {
  ConfirmCancelData,
  ConfirmReservationCancelDialog,
} from '../confirm-cancel-dialog/confirm-reservation-cancel-dialog.component';
import {
  ExtendRentalDialog,
  ExtendRentalDialogData,
} from '../extend-rental-dialog/extend-rental-dialog';

export interface ActiveBooksDialogData {
  type: 'rent' | 'reservation';
  data: ProfileBookItem[];
  onCancelReservation?: (reservationId: number) => void;
}

@Component({
  selector: 'app-active-books-dialog',
  standalone: true,
  imports: [MatButtonModule, ReactiveFormsModule, ProfileBookItemComponent],
  templateUrl: './active-books-dialog.html',
})
export class ActiveBooksDialog implements OnInit {
  dialog = inject(MatDialog);
  dialogRef = inject<MatDialogRef<ActiveBooksDialog>>(MatDialogRef);
  data = inject<ActiveBooksDialogData>(MAT_DIALOG_DATA);

  userCancelledReservation = signal<number[]>([]);

  public dialogType: 'rent' | 'reservation';

  public activeItems = signal<ProfileBookItem[]>([]);

  constructor() {
    const data = this.data;

    this.dialogType = data.type;
  }

  ngOnInit(): void {
    this.activeItems.set(this.data.data);
  }

  onNoClick(): void {
    this.dialogRef.close(null);
  }
  onRentalExtended(rental: ExtendDialogResponse): void {
    this.openExtendRentalDialog(rental);
  }

  private openExtendRentalDialog(rental: ExtendDialogResponse): void {
    const item = this.activeItems().find((i) => i.item.itemId === rental.itemId);
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
        this.dialogRef.close(true);
      }
    });
  }

  openConfirmCancelReservationDialog(reservationId: number): void {
    const item = this.activeItems().find((i) => i.id === reservationId);

    const data = {
      reservationId: reservationId,
      bookTitle: item?.item.title,
    } as ConfirmCancelData;

    const dialogRef = this.dialog.open(ConfirmReservationCancelDialog, {
      width: '450px',
      data: data,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        this.activeItems.set(this.activeItems().filter((item) => item.id !== reservationId));

        if (this.data.onCancelReservation) this.data.onCancelReservation(reservationId);

        if (this.activeItems().length === 0) {
          this.dialogRef.close(true);
        }
      }
    });
  }
}
