import {Component, inject, Input} from '@angular/core';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {SingleBook} from '../../types';
import {BookDetailsComponent} from '../book-details/book-details';

@Component({
  selector: 'single-book',
  standalone: true,
  imports: [MatDialogModule],
  templateUrl: './single-book.html'
})
export class SingleBookComponent {

  private dialog = inject(MatDialog);

  @Input() book!: SingleBook;
  @Input() imgSrc!: string;
  @Input() title!: string;
  @Input() author!: string;

  openDetails(): void {
    this.dialog.open(BookDetailsComponent, {
      data: this.book,
      panelClass: 'custom-dialog-container',
      maxWidth: '90vw',
      maxHeight: '90vh',
      autoFocus: false
    });
  }
}
