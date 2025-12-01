import { Component, inject, OnInit, signal } from '@angular/core';
import { NgOptimizedImage } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { SingleBook } from '../../types'; // Używam typu zdefiniowanego dla API
import { BookService } from '../../services/book.service';

@Component({
  selector: 'app-popular-list',
  standalone: true,
  imports: [NgOptimizedImage, MatIcon],
  templateUrl: './popular-list.html',
  styleUrl: './popular-list.scss',
})
export class PopularList implements OnInit {
  private bookService = inject(BookService);

  // Ścieżka do placeholdera (gdy brak okładki)
  readonly placeholder = 'assets/book-placeholder.svg';

  // Sygnały dla reaktywności
  popularBooks = signal<SingleBook[]>([]);
  isLoading = signal(true);

  // Mapa do śledzenia stanu ładowania konkretnych okładek (dla efektu skeleton/spinnera na każdym zdjęciu osobno)
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
      error: (err) => {
        console.error('Błąd podczas pobierania popularnych książek:', err);
        this.isLoading.set(false);
      },
    });
  }


  /**
   * Zwraca URL okładki lub placeholder, jeśli URL jest pusty/null
   */
  getImageUrl(book: SingleBook): string {
    return book.imageUrl || this.placeholder;
  }

  /**
   * Sprawdza, czy obrazek dla danej książki wciąż się ładuje
   */
  isImageLoading(bookId: number): boolean {
    return this.imageLoadingStates.get(bookId) ?? true;
  }

  /**
   * Wywoływane, gdy obrazek załaduje się poprawnie (event (load))
   */
  handleImageLoad(bookId: number): void {
    this.imageLoadingStates.set(bookId, false);
  }

  /**
   * Wywoływane, gdy wystąpi błąd ładowania obrazka (event (error))
   * Podmienia źródło na placeholder i wyłącza stan ładowania
   */
  handleImageError(event: Event, book: SingleBook): void {
    const imgElement = event.target as HTMLImageElement;

    if (!imgElement.src.includes('book-placeholder.svg')) {
      imgElement.src = this.placeholder;
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
