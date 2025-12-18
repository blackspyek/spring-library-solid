import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RentalService } from '../../services/rental-service';
import { RentalHistoryItem } from '../../types';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RentalDescriptionPipe } from '../../pipes/rental-description.pipe';
import { HistoryItemComponent } from '../history-item/history-item';

@Component({
  selector: 'app-recent-rentals-list',
  standalone: true,
  imports: [CommonModule, MatProgressSpinnerModule, RentalDescriptionPipe, HistoryItemComponent],
  templateUrl: './recent-rentals-list.html',
})
export class RecentRentalsListComponent implements OnInit {
  private rentalService = inject(RentalService);

  recentRentals = signal<RentalHistoryItem[]>([]);
  isLoading = signal(false);

  ngOnInit(): void {
    this.loadRecentRentals();
  }

  private loadRecentRentals(): void {
    this.isLoading.set(true);
    this.rentalService.getRecentHistory(3).subscribe({
      next: (rentals) => {
        this.recentRentals.set(rentals);
        this.isLoading.set(false);
      },
      error: () => {
        this.recentRentals.set([]);
        this.isLoading.set(false);
      },
    });
  }
}
