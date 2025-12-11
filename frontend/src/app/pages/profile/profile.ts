import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { ProfileCardComponent } from '../../components/profile-card/profile-card';
import { ProfileBookItemComponent } from '../../components/profile-book-item/profile-book-item';
import { HistoryItemComponent } from '../../components/history-item/history-item';
import { QrCodeComponent } from '../../components/qr-code/qr-code';
import { LibraryDetailsComponent } from '../../components/library-details/library-details';
import { PasswordChangeDialog } from '../../components/password-change-dialog/password-change-dialog';
import { NotificationSettingsDialog } from '../../components/notification-settings-dialog/notification-settings-dialog';
import { UserRentalsContainer } from '../../components/user-rentals-container/user-rentals-container';
import { UserReservationsContainer } from '../../components/user-reservations-container/user-reservations-container';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    ProfileCardComponent,
    ProfileBookItemComponent,
    HistoryItemComponent,
    QrCodeComponent,
    LibraryDetailsComponent,
    UserRentalsContainer,
    UserReservationsContainer,
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class Profile {
  constructor(public dialog: MatDialog) {}

  openPasswordChangeDialog(): void {
    const dialogRef = this.dialog.open(PasswordChangeDialog, {
      width: '600px',
      panelClass: 'password-change-dialog',
      autoFocus: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        console.log('Zmiana hasła zakończona pomyślnie.');
      }
    });
  }

  openNotificationSettingsDialog(): void {
    const dialogRef = this.dialog.open(NotificationSettingsDialog, {
      width: '600px',
      panelClass: 'notification-settings-dialog',
      autoFocus: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        console.log('Zmiana ustawień powiadomień zakończona pomyślnie');
      }
    });
  }
}
