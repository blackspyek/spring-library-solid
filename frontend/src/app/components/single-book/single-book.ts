import { Component, inject, Input } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { NgOptimizedImage } from '@angular/common';
import { SingleBook } from '../../types';
import { BookDetailsComponent } from '../book-details/book-details';

@Component({
  selector: 'app-single-book',
  standalone: true,
  imports: [MatDialogModule, NgOptimizedImage],
  templateUrl: './single-book.html',
})
export class SingleBookComponent {
  private dialog = inject(MatDialog);

  readonly placeholder = 'assets/book-placeholder.svg';

  @Input() book!: SingleBook;
  @Input() imgSrc!: string;
  @Input() title!: string;
  @Input() author!: string;

  isLoading = true;
  currentImgSrc = '';

  ngOnChanges(): void {
    this.currentImgSrc = this.imgSrc || this.placeholder;
    this.isLoading = true;
  }

  handleImageLoad(): void {
    this.isLoading = false;
  }

  handleImageError(event: Event): void {
    const imgElement = event.target as HTMLImageElement;
    if (imgElement.src !== this.placeholder && !imgElement.src.endsWith('book-placeholder.svg')) {
      this.currentImgSrc = this.placeholder;
    }
    this.isLoading = false;
  }

  openDetails(): void {
    this.dialog.open(BookDetailsComponent, {
      data: this.book,
      panelClass: 'custom-dialog-container',
      maxWidth: '90vw',
      maxHeight: '90vh',
      autoFocus: false,
    });
  }
}
