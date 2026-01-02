import { Component, input, computed, ChangeDetectionStrategy, inject, output } from '@angular/core';
import { DatePipe, NgOptimizedImage } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

import { ExtendDialogResponse } from '../../types';

type ItemStatus = 'rent' | 'reservation';

@Component({
  selector: 'app-profile-book-item',
  standalone: true,
  imports: [DatePipe, NgOptimizedImage, MatDialogModule],
  templateUrl: './profile-book-item.html',
  styleUrl: './profile-book-item.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProfileBookItemComponent {
  private dialog = inject(MatDialog);

  itemId = input.required<number>();
  title = input.required<string>();
  author = input.required<string>();
  date = input.required<string>();
  coverText = input.required<string>();
  imageUrl = input<string>();
  statusType = input<ItemStatus>('rent');
  branchId = input.required<number>();
  rentExtended = input<boolean>(false);

  // Outputs remain the same
  extendRental = output<ExtendDialogResponse>();
  cancelReservation = output<number>();

  // Computed signals now reference signal inputs
  coverColor = computed(() => {
    return this.statusType() === 'rent' ? 'var(--base-green)' : 'var(--base-purple)';
  });

  statusTextColorClass = computed(() => {
    return this.statusType() === 'rent'
      ? 'text-[color:var(--base-green)]'
      : 'text-[color:var(--base-red)]';
  });

  statusLabel = computed(() => {
    return this.statusType() === 'rent' ? 'Data zwrotu' : 'Gotowa do odbioru od';
  });

  buttonLabel = computed(() => {
    if (this.statusType() === 'rent') {
      return this.rentExtended() ? 'Przedłużono' : 'Prolonguj';
    }
    return 'Anuluj';
  });

  buttonClasses = computed(() => {
    if (this.statusType() === 'rent') {
      if (this.rentExtended()) {
        return 'bg-gray-400 text-white border-gray-400 cursor-not-allowed opacity-60';
      }
      return 'bg-[color:var(--base-green)] text-white hover:bg-[color:var(--base-green-darker)] border-[color:var(--base-green)]';
    } else {
      return 'bg-[color:var(--cancel-button)] text-[color:var(--base-red)] hover:bg-[color:var(--base-red-light)] border-[color:var(--base-red)]';
    }
  });

  onButtonClick() {
    if (this.statusType() === 'rent') {
      if (this.rentExtended()) return;
      this.extendRental.emit({
        itemId: this.itemId(),
        branchId: this.branchId(),
      } as ExtendDialogResponse);
    } else {
      this.cancelReservation.emit(this.itemId());
    }
  }
}
