import {ChangeDetectionStrategy, Component, effect, OnInit, signal} from '@angular/core';
import { CommonModule } from '@angular/common';
import { CustomSelect } from '../../components/custom-select/custom-select';
import { SelectOption, SingleBook } from '../../types';
import { SingleBookComponent } from '../../components/single-book/single-book';
import { BookService } from '../../services/book.service';
import { FormsModule } from '@angular/forms';
import { debounceTime, Subject, forkJoin } from 'rxjs';
import {SortSelectComponent} from '../../components/sort-select/sort-select';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'catalog',
  imports: [CommonModule, CustomSelect, SingleBookComponent, FormsModule, SortSelectComponent],
  templateUrl: './catalog.html',
  styleUrl: './catalog.scss',
  changeDetection: ChangeDetectionStrategy.Default
})
export class Catalog implements OnInit {
  books: SingleBook[] = [];
  currentPage: number = 0;
  totalPages: number = 0;
  totalElements: number = 0;
  pageSize: number = 16;

  searchQuery: string = '';
  selectedStatus: string | number | null = null;
  selectedPublisher: string | number | null = null;
  selectedGenre: string | null = null;

  private searchSubject = new Subject<void>();

  genres: string[] = [];
  otherGenres: SelectOption[] = [];
  publishers: SelectOption[] = [];
  statusOptions: SelectOption[] = [];
  sortBy: 'TITLE_ASC' | 'TITLE_DESC' | 'AUTHOR_ASC' | 'AUTHOR_DESC' | null = null;

  currentSelectionOther = signal<SelectOption | null>(null);
  currentSelectionStatus = signal<SelectOption | null>(null);
  currentSelectionPublisher = signal<SelectOption | null>(null);

  constructor(private bookService: BookService, private route:ActivatedRoute) {
    effect(() => {
      const statusOpt = this.currentSelectionStatus();
      const newStatus = statusOpt ? String(statusOpt.value) : null;

      if (this.selectedStatus !== newStatus) {
        this.selectedStatus = newStatus;
        this.currentPage = 0;
        this.loadBooks();
      }
    });

    effect(() => {
      const pubOpt = this.currentSelectionPublisher();
      const newPub = pubOpt ? String(pubOpt.value) : null;

      if (this.selectedPublisher !== newPub) {
        this.selectedPublisher = newPub;
        this.currentPage = 0;
        this.loadBooks();
      }
    });

    effect(() => {
      const otherOpt = this.currentSelectionOther();
      if (otherOpt && otherOpt.value) {
        this.setGenre(String(otherOpt.value));
        setTimeout(() => this.currentSelectionOther.set(null), 100);
      }
    });
  }

  ngOnInit() {

    this.route.queryParamMap.subscribe(params => {
      const q = params.get('q');
      if (q !== null) {
        this.searchQuery = q;
        this.currentPage = 0;
        this.loadBooks();
      }
    });

    this.searchSubject.pipe(debounceTime(500)).subscribe(() => {
      this.currentPage = 0;
      this.loadBooks();
    });

    this.loadFiltersAndBooks();

  }


  loadFiltersAndBooks() {
    forkJoin({
      genres: this.bookService.getTopGenres(),
      otherGenres: this.bookService.getOtherGenres(),
      publishers: this.bookService.getAllPublishers(),
      statuses: this.bookService.getAllStatuses()
    }).subscribe({
      next: (data) => {

        this.genres = data.genres;

        this.otherGenres = data.otherGenres.map(genre => ({
          label: genre,
          value: genre
        }));

        this.publishers = data.publishers.map(pub => ({
          label: pub,
          value: pub
        }));

        this.statusOptions = data.statuses.map(status => ({
          label: this.getStatusLabel(status),
          value: status
        }));

        this.loadBooks();
      },
      error: (error) => {
        console.error('Error loading filters:', error);
        this.loadBooks();
      }
    });
  }

  getStatusLabel(status: string): string {
    const labels: { [key: string]: string } = {
      'AVAILABLE': 'Dostępny',
      'RENTED': 'Wypożyczony',
      'MAINTENANCE': 'W konserwacji',
      'LOST': 'Zgubiony'
    };
    return labels[status] || status;
  }

  loadBooks() {
    this.bookService.searchBooks(
      this.searchQuery || undefined,
      this.selectedStatus !== null ? String(this.selectedStatus) : undefined,
      this.selectedPublisher !== null ? String(this.selectedPublisher) : undefined,
      this.selectedGenre ? [this.selectedGenre] : undefined,
      this.currentPage,
      this.pageSize,
      this.sortBy || undefined
    ).subscribe({
      next: (response) => {
        this.books = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
      },
      error: (error) => {
        console.error('Error loading books:', error);
      }
    });
  }

  onSortChange(sort: string | null) {
    this.sortBy = sort as 'TITLE_ASC' | 'TITLE_DESC' | 'AUTHOR_ASC' | 'AUTHOR_DESC' | null;
    this.currentPage = 0;
    this.loadBooks();
  }

  onSearchChange() {
    this.searchSubject.next();
  }

  setGenre(genre: string) {

    if (this.selectedGenre === genre) {
      this.selectedGenre = null;
    } else {
      this.selectedGenre = genre;
    }

    this.currentPage = 0;
    this.loadBooks();
  }

  isGenreSelected(genre: string): boolean {
    return this.selectedGenre === genre;
  }


  goToPage(page: number) {
    if (page < 0 || page >= this.totalPages || page === this.currentPage) {
      return;
    }
    this.currentPage = page;
    this.loadBooks();
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadBooks();
    }
  }

  previousPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadBooks();
    }
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxVisible = 4;
    let startPage = Math.max(0, this.currentPage - 2);
    let endPage = Math.min(this.totalPages - 1, startPage + maxVisible - 1);

    if (endPage - startPage < maxVisible - 1) {
      startPage = Math.max(0, endPage - maxVisible + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    return pages;
  }
}
