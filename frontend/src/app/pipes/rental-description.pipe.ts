import { Pipe, PipeTransform } from '@angular/core';
import { RentalHistoryItem } from '../types';

@Pipe({
  name: 'rentalDescription',
  standalone: true,
  pure: true,
})
export class RentalDescriptionPipe implements PipeTransform {
  transform(rental: RentalHistoryItem): string {
    if (rental.itemAuthor && rental.itemAuthor !== '-') {
      return `${rental.itemTitle} (${rental.itemAuthor})`;
    }
    return rental.itemTitle;
  }
}
