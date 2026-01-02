import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
  MatDialog,
} from '@angular/material/dialog';
import { SingleBook, LibrarySelectorDialogData, LibraryBranch } from '../../types';
import { MatIconModule } from '@angular/material/icon';
import { BookService } from '../../services/book.service';
import { LibrarySelectorDialog } from '../library-selector-dialog/library-selector-dialog';
import { AuthService } from '../../services/auth.service';
import { Router, RouterLink } from '@angular/router';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-book-details',
  standalone: true,
  imports: [CommonModule, MatDialogModule, NgOptimizedImage, MatIconModule, RouterLink],
  templateUrl: './book-details.html',
})
export class BookDetailsComponent implements OnInit {
  readonly dialogRef = inject(MatDialogRef<BookDetailsComponent>);
  readonly data = inject<SingleBook>(MAT_DIALOG_DATA);
  private dialog = inject(MatDialog);
  private userService = inject(UserService);
  private bookService = inject(BookService);
  private authService = inject(AuthService);
  private router = inject(Router);

  favouriteBranch = signal<LibraryBranch | null>(null);
  isAvailableAtFavourite = signal<boolean | null>(null);
  favouriteBranchName = signal<string>('');
  showLoginPrompt = signal(false);

  ngOnInit(): void {
    this.loadFavouriteBranchAvailability();
  }

  private loadFavouriteBranchAvailability(): void {
    if (!this.authService.isLoggedIn()) {
      this.showLoginPrompt.set(true);
      return;
    }

    this.userService.getFavouriteBranch().subscribe({
      next: (branch) => {
        this.favouriteBranch.set(branch);
        this.favouriteBranchName.set(
          `Miejska Biblioteka Publiczna im. H. Łopacińskiego Filia nr ${branch.branchNumber}`
        );

        this.bookService.getBookAvailability(this.data.id).subscribe({
          next: (availability) => {
            const isAvailable =
              availability.availableAtBranches?.includes(branch.id) ?? false;
            this.isAvailableAtFavourite.set(isAvailable && availability.status === 'AVAILABLE');
          },
          error: () => {
            this.isAvailableAtFavourite.set(false);
          },
        });
      },
      error: () => {
        this.favouriteBranchName.set('Nie ustawiono ulubionej biblioteki');
        this.isAvailableAtFavourite.set(false);
      },
    });
  }

  close(): void {
    this.dialogRef.close();
  }

  getStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      AVAILABLE: 'Dostępny',
      RENTED: 'Wypożyczony',
      RESERVED: 'Zarezerwowany',
      MAINTENANCE: 'W konserwacji',
      LOST: 'Zgubiony',
    };
    return labels[status] || status;
  }

  openReservationDialog(): void {
    // Check if user is logged in
    if (!this.authService.isLoggedIn()) {
      this.dialogRef.close();
      void this.router.navigate(['/zaloguj-sie']);
      return;
    }

    this.bookService.getBookAvailability(this.data.id).subscribe({
      next: (availability) => {
        const dialogData: LibrarySelectorDialogData = {
          mode: 'availability',
          bookTitle: availability.title,
          bookId: availability.id,
          availableBranchIds: availability.availableAtBranches,
        };

        const dialogRef = this.dialog.open(LibrarySelectorDialog, {
          data: dialogData,
          panelClass: 'custom-dialog-container',
          maxWidth: '90vw',
          autoFocus: false,
        });

        dialogRef.afterClosed().subscribe((result) => {
          if (result?.success) {
            this.dialogRef.close({ reserved: true });
          }
        });
      },
      error: (err) => {
        console.error('Failed to load book availability:', err);
      },
    });
  }
}
