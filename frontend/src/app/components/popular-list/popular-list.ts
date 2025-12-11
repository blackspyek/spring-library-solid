import { Component, inject, OnInit, signal } from '@angular/core';
import { NgOptimizedImage } from '@angular/common';
import { SingleBook } from '../../types';
import { MatIcon } from '@angular/material/icon';
import { BookService } from '../../services/book.service';

@Component({
  selector: 'app-popular-list',
  imports: [NgOptimizedImage, MatIcon],
  templateUrl: './popular-list.html',
  styleUrl: './popular-list.scss',
})
export class PopularList implements OnInit {
  private bookService = inject(BookService);

  readonly placeholder = 'assets/book-placeholder.svg';

  popularBooks = signal<SingleBook[]>([]);
  isLoading = signal(true);
  imageLoadingStates = new Map<number, boolean>();

  ngOnInit(): void {
    this.loadPopularBooks();
  }

  private loadPopularBooks(): void {
    this.bookService.getPopularBooks(12).subscribe({
      next: (books) => {
        this.popularBooks.set(books);
        books.forEach((book) => this.imageLoadingStates.set(book.id, true));
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      },
    });
  }

  getImageUrl(book: SingleBook): string {
    return book.imageUrl || this.placeholder;
  }

  isImageLoading(bookId: number): boolean {
    return this.imageLoadingStates.get(bookId) ?? true;
  }

  handleImageLoad(bookId: number): void {
    this.imageLoadingStates.set(bookId, false);
  }

  handleImageError(event: Event, book: SingleBook): void {
    const imgElement = event.target as HTMLImageElement;
    if (!imgElement.src.endsWith('book-placeholder.svg')) {
      book.imageUrl = this.placeholder;
    }
    this.imageLoadingStates.set(book.id, false);
  }

  scrollLeft(element: HTMLElement): void {
    const scrollAmount = element.clientWidth * 0.8;
    element.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
  }

  scrollRight(element: HTMLElement): void {
    const scrollAmount = element.clientWidth * 0.8;
    element.scrollBy({ left: scrollAmount, behavior: 'smooth' });
  }
}
