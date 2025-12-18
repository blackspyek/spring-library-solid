import { Component, inject, Input } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { NgOptimizedImage } from '@angular/common';
import { SingleBook, LibrarySelectorDialogData } from '../../types';
import { BookDetailsComponent } from '../book-details/book-details';
import { LibrarySelectorDialog } from '../library-selector-dialog/library-selector-dialog';
import { BookService } from '../../services/book.service';
import { AuthService } from '../../services/auth-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-single-book',
  standalone: true,
  imports: [MatDialogModule, NgOptimizedImage],
  templateUrl: './single-book.html',
})
export class SingleBookComponent {
  private dialog = inject(MatDialog);
  private bookService = inject(BookService);
  private authService = inject(AuthService);
  private router = inject(Router);

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

  openAvailabilityDialog(): void {
    this.bookService.getBookAvailability(this.book.id).subscribe({
      next: (availability) => {
        const dialogData: LibrarySelectorDialogData = {
          mode: 'availability',
          bookTitle: availability.title,
          bookId: availability.id,
          availableBranches: availability.availableAtBranches,
        };

        this.dialog.open(LibrarySelectorDialog, {
          data: dialogData,
          panelClass: 'custom-dialog-container',
          maxWidth: '90vw',
          autoFocus: false,
        });
      },
      error: (err) => {
        console.error('Failed to load book availability:', err);
      },
    });
  }
}

