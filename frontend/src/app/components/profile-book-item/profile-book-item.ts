import {
  Component,
  Input,
  Output,
  EventEmitter,
  computed,
  ChangeDetectionStrategy,
  inject,
} from '@angular/core';
import { CommonModule, DatePipe, NgOptimizedImage } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import {
  ExtendRentalDialog,
  ExtendRentalDialogData,
} from '../extend-rental-dialog/extend-rental-dialog';

type ItemStatus = 'rent' | 'reservation';

@Component({
  selector: 'app-profile-book-item',
  standalone: true,
  imports: [CommonModule, DatePipe, NgOptimizedImage, MatDialogModule],
  templateUrl: './profile-book-item.html',
  styleUrl: './profile-book-item.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProfileBookItemComponent {
  private dialog = inject(MatDialog);

  @Input() itemId!: number;
  @Input() title!: string;
  @Input() author!: string;
  @Input() date!: string;
  @Input() coverText!: string;
  @Input() imageUrl?: string;
  @Input() statusType: ItemStatus = 'rent';

  @Output() rentalExtended = new EventEmitter<number>();

  coverColor = computed(() => {
    return this.statusType === 'rent' ? 'var(--base-green)' : 'var(--base-purple)';
  });

  statusTextColorClass = computed(() => {
    return this.statusType === 'rent'
      ? 'text-[color:var(--base-green)]'
      : 'text-[color:var(--base-red)]';
  });

  statusLabel = computed(() => {
    return this.statusType === 'rent' ? 'Data zwrotu' : 'Gotowa do odbioru od';
  });

  buttonLabel = computed(() => {
    return this.statusType === 'rent' ? 'Prolonguj' : 'Anuluj';
  });

  buttonClasses = computed(() => {
    if (this.statusType === 'rent') {
      return 'bg-[color:var(--base-green)] text-white hover:bg-[color:var(--base-green-darker)] border-[color:var(--base-green)]';
    } else {
      return 'bg-[color:var(--cancel-button)] text-[color:var(--base-red)] hover:bg-[color:var(--base-red-light)] border-[color:var(--base-red)]';
    }
  });

  onButtonClick() {
    if (this.statusType === 'rent') {
      this.openExtendRentalDialog();
    } else {
      // TODO: anuluj rezerwację
      console.log(`Anuluj dla: ${this.title}`);
    }
  }

  private openExtendRentalDialog(): void {
    const dialogData: ExtendRentalDialogData = {
      itemId: this.itemId,
      bookTitle: this.title,
    };

    const dialogRef = this.dialog.open(ExtendRentalDialog, {
      width: '500px',
      panelClass: 'extend-rental-dialog',
      autoFocus: true,
      data: dialogData,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        this.rentalExtended.emit(this.itemId);
      }
    });
  }
}
