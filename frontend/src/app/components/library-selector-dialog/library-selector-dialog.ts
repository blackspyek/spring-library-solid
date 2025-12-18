import {
  Component,
  OnInit,
  AfterViewInit,
  OnDestroy,
  inject,
  signal,
  computed,
  ElementRef,
  ViewChild,
} from '@angular/core';

import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatRadioModule } from '@angular/material/radio';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CommonModule } from '@angular/common';
import * as L from 'leaflet';
import { LibraryBranch, LibrarySelectorDialogData } from '../../types';
import { BranchService } from '../../services/branch-service';
import { ReservationService } from '../../services/reservation.service';
import { AuthService } from '../../services/auth-service';
import { Router } from '@angular/router';

// Fix for default marker icons in Leaflet with Angular
const iconRetinaUrl = 'assets/marker-icon-2x.png';
const iconUrl = 'assets/marker-icon.png';
const shadowUrl = 'assets/marker-shadow.png';

@Component({
  selector: 'app-library-selector-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatRadioModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './library-selector-dialog.html',
  styleUrl: './library-selector-dialog.scss',
})
export class LibrarySelectorDialog implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('mapContainer', { static: false }) mapContainer!: ElementRef;
  @ViewChild('branchListContainer', { static: false }) branchListContainer!: ElementRef;

  private dialogRef = inject(MatDialogRef<LibrarySelectorDialog>);
  private branchService = inject(BranchService);
  private reservationService = inject(ReservationService);
  private authService = inject(AuthService);
  private router = inject(Router);
  public data: LibrarySelectorDialogData = inject(MAT_DIALOG_DATA);

  private map: L.Map | null = null;
  private markers: L.Marker[] = [];
  private defaultIcon!: L.Icon;
  private selectedIcon!: L.Icon;

  searchQuery = signal('');
  allBranches = signal<LibraryBranch[]>([]);
  selectedBranchId = signal<number | null>(null);
  isLoading = signal(false);
  showConfirmation = signal(false);
  showSuccess = signal(false);
  reservationExpiresAt = signal<string | null>(null);
  errorMessage = signal<string | null>(null);

  filteredBranches = computed(() => {
    const query = this.searchQuery().toLowerCase().trim();
    let branches = this.allBranches();

    // In availability mode, filter to only available branches if provided
    if (this.data.mode === 'availability' && this.data.availableBranches) {
      const availableIds = new Set(this.data.availableBranches.map((b) => b.id));
      branches = branches.filter((b) => availableIds.has(b.id));
    }

    // Apply search filter
    if (query) {
      branches = branches.filter(
        (b) =>
          b.city.toLowerCase().includes(query) ||
          b.address.toLowerCase().includes(query) ||
          b.branchNumber.toLowerCase().includes(query) ||
          (b.name && b.name.toLowerCase().includes(query))
      );
    }

    return branches;
  });

  get dialogTitle(): string {
    if (this.data.mode === 'availability') {
      return `Dostępność: ${this.data.bookTitle || 'Książka'}`;
    }
    return 'Wybierz Swoją Ulubioną Bibliotekę';
  }

  get dialogSubtitle(): string {
    if (this.data.mode === 'availability') {
      return 'Wybierz bibliotekę w której wybrana książka jest dostępna.';
    }
    return 'Wyszukaj miasto, kod pocztowy lub numer filii, aby znaleźć bibliotekę, lub wybierz punkt na mapie.';
  }

  get confirmButtonText(): string {
    if (this.data.mode === 'availability') {
      return 'ZAREZERWUJ';
    }
    return 'ZAPISZ ZMIANY';
  }

  ngOnInit(): void {
    // Initialize icons
    this.defaultIcon = L.icon({
      iconRetinaUrl,
      iconUrl,
      shadowUrl,
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      shadowSize: [41, 41],
    });

    this.selectedIcon = L.icon({
      iconRetinaUrl,
      iconUrl,
      shadowUrl,
      iconSize: [30, 49],
      iconAnchor: [15, 49],
      popupAnchor: [1, -34],
      shadowSize: [41, 41],
      className: 'selected-marker',
    });

    // Set initial selection for favorite mode
    if (this.data.mode === 'favorite' && this.data.currentFavouriteBranchId) {
      this.selectedBranchId.set(this.data.currentFavouriteBranchId);
    }

    // Load branches - use provided data or fetch from API
    if (this.data.allBranches && this.data.allBranches.length > 0) {
      this.allBranches.set(this.data.allBranches);
      setTimeout(() => this.updateMapMarkers(), 100);
    } else {
      this.branchService.getAllBranches().subscribe({
        next: (branches) => {
          this.allBranches.set(branches);
          setTimeout(() => this.updateMapMarkers(), 100);
        },
        error: (err) => {
          console.error('Failed to load branches:', err);
          // If API fails and we have availableBranches, use those
          if (this.data.availableBranches) {
            this.allBranches.set(this.data.availableBranches);
            setTimeout(() => this.updateMapMarkers(), 100);
          }
        },
      });
    }
  }

  ngAfterViewInit(): void {
    this.initMap();
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
    }
  }

  private initMap(): void {
    // Center on Lublin
    const lublinCenter: L.LatLngExpression = [51.2465, 22.5684];

    this.map = L.map(this.mapContainer.nativeElement, {
      center: lublinCenter,
      zoom: 13,
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution:
        '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    }).addTo(this.map);

    // Fix map container size issue
    setTimeout(() => {
      this.map?.invalidateSize();
    }, 100);
  }

  private updateMapMarkers(): void {
    if (!this.map) return;

    // Clear existing markers
    this.markers.forEach((marker) => marker.remove());
    this.markers = [];

    // Add markers for filtered branches
    const branches = this.filteredBranches();
    branches.forEach((branch) => {
      const isSelected = branch.id === this.selectedBranchId();
      const marker = L.marker([branch.latitude, branch.longitude], {
        icon: isSelected ? this.selectedIcon : this.defaultIcon,
      })
        .addTo(this.map!)
        .bindPopup(`<strong>Filia nr ${branch.branchNumber}</strong><br>${branch.address}`);

      marker.on('click', () => {
        this.selectBranch(branch);
      });

      this.markers.push(marker);
    });

    // Fit bounds to show all markers
    if (this.markers.length > 0) {
      const group = L.featureGroup(this.markers);
      this.map.fitBounds(group.getBounds().pad(0.1));
    }
  }

  onSearchChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchQuery.set(input.value);
    this.updateMapMarkers();
  }

  selectBranch(branch: LibraryBranch): void {
    this.selectedBranchId.set(branch.id);
    this.updateMapMarkers();

    // Pan map to selected branch
    if (this.map) {
      this.map.setView([branch.latitude, branch.longitude], 15);
    }

    // Scroll to selected item in the list
    this.scrollToSelectedBranch(branch.id);
  }

  private scrollToSelectedBranch(branchId: number): void {
    setTimeout(() => {
      const element = document.getElementById(`branch-item-${branchId}`);
      if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
      }
    }, 50);
  }

  isBranchSelected(branch: LibraryBranch): boolean {
    return branch.id === this.selectedBranchId();
  }

  getBranchDisplayName(branch: LibraryBranch): string {
    let name = `Filia nr ${branch.branchNumber}`;
    if (this.data.mode === 'favorite' && branch.id === this.data.currentFavouriteBranchId) {
      name += ' (AKTYWNA)';
    }
    return name;
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  onConfirm(): void {
    const selectedId = this.selectedBranchId();
    if (!selectedId) return;

    // In availability mode, check auth before reserving
    if (this.data.mode === 'availability') {
      if (!this.authService.isLoggedIn()) {
        this.dialogRef.close();
        void this.router.navigate(['/zaloguj-sie']);
        return;
      }
      this.showConfirmation.set(true);
      return;
    }

    // For favorite mode, just return the selected branch
    const selectedBranch = this.allBranches().find((b) => b.id === selectedId);
    this.dialogRef.close(selectedBranch);
  }

  onConfirmReservation(): void {
    const branchId = this.selectedBranchId();
    const bookId = this.data.bookId;

    if (!branchId || !bookId) {
      this.errorMessage.set('Brak wymaganych danych do rezerwacji');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.reservationService.createReservation({ libraryItemId: bookId, branchId }).subscribe({
      next: (reservation) => {
        this.isLoading.set(false);
        this.reservationExpiresAt.set(reservation.expiresAt);
        this.showConfirmation.set(false);
        this.showSuccess.set(true);
      },
      error: (err) => {
        this.isLoading.set(false);
        const message = err.error?.message || err.message || 'Wystąpił błąd podczas rezerwacji';
        this.errorMessage.set(message);
      },
    });
  }

  closeSuccess(): void {
    this.dialogRef.close({ success: true });
  }

  onCancelConfirmation(): void {
    this.showConfirmation.set(false);
    this.errorMessage.set(null);
    // Reinitialize map after DOM renders the main content
    setTimeout(() => {
      this.initMap();
      this.updateMapMarkers();
    }, 100);
  }

  getSelectedBranchName(): string {
    const selectedId = this.selectedBranchId();
    if (!selectedId) return '';
    const branch = this.allBranches().find((b) => b.id === selectedId);
    return branch ? `Filia nr ${branch.branchNumber} - ${branch.address}, ${branch.city}` : '';
  }
}
