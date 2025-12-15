import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';

@Component({
  selector: 'app-library-details',
  standalone: true,
  imports: [],
  templateUrl: './library-details.html',
  styleUrl: './library-details.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LibraryDetailsComponent {
  @Input() libraryName!: string;
  @Input() libraryAddress!: string;
  @Input() openingDays: string[] = [];
  @Input() openingHours: string[] = [];
  @Output() changeLibrary = new EventEmitter<void>();

  onChangeLibraryClick() {
    this.changeLibrary.emit();
  }
}
