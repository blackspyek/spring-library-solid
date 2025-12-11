import { Component, Input, computed, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule, DatePipe, NgOptimizedImage } from '@angular/common';

type ItemStatus = 'rent' | 'reservation';

@Component({
  selector: 'app-profile-book-item',
  standalone: true,
  imports: [CommonModule, DatePipe, NgOptimizedImage],
  templateUrl: './profile-book-item.html',
  styleUrl: './profile-book-item.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProfileBookItemComponent {
  @Input() title!: string;
  @Input() author!: string;
  @Input() date!: string;
  @Input() coverText!: string;
  @Input() imageUrl?: string;
  @Input() statusType: ItemStatus = 'rent';

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
      return 'bg-[color:var(--base-green)] text-[color:var(--base-white)] hover:bg-[color:var(--base-green-darker)] border-[color:var(--base-green)]';
    } else {
      return 'bg-[color:var(--base-gray-light)] text-[color:var(--base-red)] hover:bg-[color:var(--base-red-light)] border-[color:var(--base-red)]';
    }
  });

  onButtonClick() {
    //todo: prolonguj lub anuluj
    console.log(`${this.buttonLabel()} dla: ${this.title}`);
  }
}
