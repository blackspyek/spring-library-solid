import { Component, inject, OnInit, signal, computed } from '@angular/core';

import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { HistoryItemComponent } from '../../components/history-item/history-item';
import { PasswordChangeDialog } from '../../components/password-change-dialog/password-change-dialog';
import { NotificationSettingsDialog } from '../../components/notification-settings-dialog/notification-settings-dialog';
import { LibraryDetailsComponent } from '../../components/library-details/library-details';
import { ProfileCardComponent } from '../../components/profile-card/profile-card';
import { QrCodeComponent } from '../../components/qr-code/qr-code';
import { UserReservationsContainer } from '../../components/user-reservations-container/user-reservations-container';
import { UserRentalsContainer } from '../../components/user-rentals-container/user-rentals-container';
import { UserService } from '../../services/user-service';
import { LibraryBranch, LibrarySelectorDialogData } from '../../types';
import { LibrarySelectorDialog } from '../../components/library-selector-dialog/library-selector-dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    MatDialogModule,
    MatButtonModule,
    HistoryItemComponent,
    LibraryDetailsComponent,
    ProfileCardComponent,
    QrCodeComponent,
    UserReservationsContainer,
    UserRentalsContainer,
    MatProgressSpinnerModule,
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class Profile implements OnInit {
  dialog = inject(MatDialog);
  private userService = inject(UserService);

  favouriteBranch = signal<LibraryBranch | null>(null);
  isLoadingBranch = signal(false);

  // Computed properties for library details display
  libraryDisplayName = computed(() => {
    const branch = this.favouriteBranch();
    if (!branch) return '';
    return `Miejska Biblioteka Publiczna<br />im. H. Łopacińskiego ${branch.name}`;
  });

  libraryDisplayAddress = computed(() => {
    const branch = this.favouriteBranch();
    if (!branch) return '';
    return `${branch.address}, ${branch.city}`;
  });

  openingDays = computed(() => {
    const branch = this.favouriteBranch();
    if (!branch?.openingHours) return [];
    return this.parseOpeningHours(branch.openingHours).days;
  });

  openingHoursList = computed(() => {
    const branch = this.favouriteBranch();
    if (!branch?.openingHours) return [];
    return this.parseOpeningHours(branch.openingHours).hours;
  });

  ngOnInit(): void {
    this.loadFavouriteBranch();
  }

  private loadFavouriteBranch(): void {
    this.isLoadingBranch.set(true);
    this.userService.getFavouriteBranch().subscribe({
      next: (branch) => {
        this.favouriteBranch.set(branch);
        this.isLoadingBranch.set(false);
      },
      error: () => {
        this.favouriteBranch.set(null);
        this.isLoadingBranch.set(false);
      },
    });
  }

  private parseOpeningHours(openingHoursStr: string): { days: string[]; hours: string[] } {
    // Format: "Poniedziałek: 09:00-18:00|Wtorek: 09:00-18:00|..."
    const days: string[] = [];
    const hours: string[] = [];

    const entries = openingHoursStr.split('|');
    for (const entry of entries) {
      const [day, time] = entry.split(': ');
      if (day && time) {
        days.push(day);
        hours.push(time);
      }
    }

    return { days, hours };
  }

  openLibrarySelectorDialog(): void {
    const dialogData: LibrarySelectorDialogData = {
      mode: 'favorite',
      currentFavouriteBranchId: this.favouriteBranch()?.id,
    };

    const dialogRef = this.dialog.open(LibrarySelectorDialog, {
      width: '900px',
      maxWidth: '95vw',
      maxHeight: '90vh',
      panelClass: 'library-selector-dialog',
      data: dialogData,
      autoFocus: true,
    });

    dialogRef.afterClosed().subscribe((selectedBranch: LibraryBranch | null) => {
      if (selectedBranch) {
        this.updateFavouriteBranch(selectedBranch);
      }
    });
  }

  private updateFavouriteBranch(branch: LibraryBranch): void {
    this.isLoadingBranch.set(true);
    this.userService.updateFavouriteBranch(branch.id).subscribe({
      next: () => {
        this.favouriteBranch.set(branch);
        this.isLoadingBranch.set(false);
      },
      error: (error) => {
        console.error('Błąd aktualizacji ulubionej biblioteki:', error);
        this.isLoadingBranch.set(false);
      },
    });
  }

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
