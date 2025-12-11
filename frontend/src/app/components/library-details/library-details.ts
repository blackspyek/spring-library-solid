import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'library-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './library-details.html',
  styleUrl: './library-details.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LibraryDetailsComponent {
  @Input() libraryName!: string;
  @Input() libraryAddress!: string;
  @Input() openingDays: string[] = [];
  @Input() openingHours: string[] = [];

  onChangeLibraryClick() {
    console.log('Kliknięto: Zmień Bibliotekę');
  }
}
