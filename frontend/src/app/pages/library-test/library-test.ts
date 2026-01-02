import { Component, inject, signal } from '@angular/core';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { LibrarySelectorDialog } from '../../components/library-selector-dialog/library-selector-dialog';
import { LibraryBranch, LibrarySelectorDialogData } from '../../types';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-library-test',
  standalone: true,
  imports: [MatButtonModule, MatCardModule, MatDialogModule],
  templateUrl: './library-test.html',
  styleUrl: './library-test.scss',
})
export class LibraryTest {
  private dialog = inject(MatDialog);
  private userService = inject(UserService);

  selectedFavouriteBranch = signal<LibraryBranch | null>(null);
  selectedAvailabilityBranch = signal<LibraryBranch | null>(null);

  mockAllBranches: LibraryBranch[] = [
    {
      id: 1,
      branchNumber: '1',
      name: 'Filia nr 1',
      city: 'Lublin',
      address: 'ul. Kościelna 7a',
      latitude: 51.2465,
      longitude: 22.5684,
    },
    {
      id: 2,
      branchNumber: '6',
      name: 'Filia nr 6 BIOTEKA MEDIATEKA',
      city: 'Lublin',
      address: 'Aleje Racławickie 22',
      latitude: 51.2463,
      longitude: 22.5312,
    },
    {
      id: 3,
      branchNumber: '12',
      name: 'Filia nr 12',
      city: 'Lublin',
      address: 'ul. Żelazowej Woli 7',
      latitude: 51.2298,
      longitude: 22.4891,
    },
    {
      id: 4,
      branchNumber: '18',
      name: 'Filia nr 18',
      city: 'Lublin',
      address: 'ul. Głęboka 8a',
      latitude: 51.2507,
      longitude: 22.5523,
    },
    {
      id: 5,
      branchNumber: '21',
      name: 'Filia nr 21',
      city: 'Lublin',
      address: 'Rynek 11',
      latitude: 51.2475,
      longitude: 22.5657,
    },
    {
      id: 6,
      branchNumber: '29',
      name: 'Filia nr 29',
      city: 'Lublin',
      address: 'ul. Kiepury 5',
      latitude: 51.2351,
      longitude: 22.4879,
    },
    {
      id: 7,
      branchNumber: '30',
      name: 'Filia nr 30',
      city: 'Lublin',
      address: 'ul. Braci Wieniawskich 5',
      latitude: 51.2375,
      longitude: 22.5002,
    },
    {
      id: 8,
      branchNumber: '31',
      name: 'Filia nr 31',
      city: 'Lublin',
      address: 'ul. Nałkowskich 104',
      latitude: 51.2276,
      longitude: 22.4934,
    },
    {
      id: 9,
      branchNumber: '32',
      name: 'Filia nr 32 BIBLIO MEDIATEKA',
      city: 'Lublin',
      address: 'ul. Szaserów 13-15',
      latitude: 51.2642,
      longitude: 22.5189,
    },
    {
      id: 10,
      branchNumber: '40',
      name: 'Filia nr 40 BIBLIOTEKA NA POZIOMIE',
      city: 'Lublin',
      address: 'ul. Sławin 20',
      latitude: 51.2712,
      longitude: 22.5098,
    },
  ];

  mockAvailableBranches: LibraryBranch[] = this.mockAllBranches.slice(0, 3);

  openFavouriteSelector(): void {
    const dialogRef = this.dialog.open(LibrarySelectorDialog, {
      maxWidth: '95vw',
      maxHeight: '95vh',
      data: {
        mode: 'favorite',
        currentFavouriteBranchId: this.selectedFavouriteBranch()?.id,
        allBranches: this.mockAllBranches,
      } as LibrarySelectorDialogData,
    });

    dialogRef.afterClosed().subscribe((result: LibraryBranch | null) => {
      if (result) {
        this.selectedFavouriteBranch.set(result);
        // Optionally save to backend
        // this.userService.updateFavouriteBranch(result.id).subscribe();
      }
    });
  }

  openAvailabilitySelector(): void {
    const dialogRef = this.dialog.open(LibrarySelectorDialog, {
      maxWidth: '95vw',
      maxHeight: '95vh',
      data: {
        mode: 'availability',
        bookTitle: 'W Głąb (2017) Katarzyna Bonda',
        availableBranches: this.mockAvailableBranches,
        allBranches: this.mockAllBranches,
      } as LibrarySelectorDialogData,
    });

    dialogRef.afterClosed().subscribe((result: LibraryBranch | null) => {
      if (result) {
        this.selectedAvailabilityBranch.set(result);
        console.log('Reserved at branch:', result);
      }
    });
  }
}
