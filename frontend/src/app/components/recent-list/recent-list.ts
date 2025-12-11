import { Component, inject, OnInit } from '@angular/core';
import { NgOptimizedImage } from '@angular/common';
import { BookService } from '../../services/book.service';
import { SingleBook } from '../../types';

@Component({
  selector: 'app-recent-list',
  imports: [NgOptimizedImage],
  templateUrl: './recent-list.html',
})
export class RecentList implements OnInit {
  private bookService = inject(BookService);

  readonly placeholder = 'assets/book-placeholder.svg';

  popularBooks: SingleBook[] = [];
  featuredBook: SingleBook | null = null;
  gridBooks: SingleBook[] = [];

  isLoading = true;
  featuredImageLoading = true;
  gridImageLoading = new Map<number, boolean>();

  ngOnInit(): void {
    this.loadPopularBooks();
  }

  private loadPopularBooks(): void {
    this.bookService.getPopularBooks(10).subscribe({
      next: (books) => {
        this.popularBooks = books;

        const bestseller = books.find((book) => book.bestseller);
        this.featuredBook = bestseller || books[0] || null;

        this.gridBooks = books.filter((book) => book.id !== this.featuredBook?.id).slice(0, 6);

        this.gridBooks.forEach((book) => this.gridImageLoading.set(book.id, true));

        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  getImageUrl(book: SingleBook): string {
    return book.imageUrl || this.placeholder;
  }

  handleFeaturedLoad(): void {
    this.featuredImageLoading = false;
  }

  handleFeaturedError(event: Event): void {
    const imgElement = event.target as HTMLImageElement;
    if (!imgElement.src.endsWith('book-placeholder.svg')) {
      if (this.featuredBook) {
        this.featuredBook.imageUrl = this.placeholder;
      }
    }
    this.featuredImageLoading = false;
  }

  handleGridImageLoad(bookId: number): void {
    this.gridImageLoading.set(bookId, false);
  }

  handleGridImageError(event: Event, book: SingleBook): void {
    const imgElement = event.target as HTMLImageElement;
    if (!imgElement.src.endsWith('book-placeholder.svg')) {
      book.imageUrl = this.placeholder;
    }
    this.gridImageLoading.set(book.id, false);
  }

  isGridImageLoading(bookId: number): boolean {
    return this.gridImageLoading.get(bookId) ?? true;
  }
}
