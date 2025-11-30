import { Component, Input } from '@angular/core';

@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'history-item',
  templateUrl: './history-item.html',
  styleUrl: './history-item.scss',
  standalone: true,
})
export class HistoryItemComponent {
  @Input() fullDescription!: string;
  @Input() dateRented!: string;
  @Input() dateReturned!: string;
}
