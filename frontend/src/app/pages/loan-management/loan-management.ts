import { ChangeDetectionStrategy, Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { LoanService } from '../../services/loan.service';
import { UserService } from '../../services/user.service';
import { SingleBook, User } from '../../types';
import { debounceTime, Subject, catchError, of, forkJoin } from 'rxjs';
import { QrScannerComponent } from '../../components/qr-scanner/qr-scanner';

@Component({
  selector: 'app-loan-management',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIcon, QrScannerComponent],
  templateUrl: './loan-management.html',
  styleUrl: './loan-management.scss',
  changeDetection: ChangeDetectionStrategy.Default,
})
export class LoanManagement implements OnInit {
  private loanService = inject(LoanService);
  private userService = inject(UserService);

  userSearchQuery = '';
  selectedUser = signal<User | null>(null);
  userLoans = signal<SingleBook[]>([]);
  userSearchError = signal<string | null>(null);
  isSearchingUser = signal(false);

  bookSearchQuery = '';
  availableBooks = signal<SingleBook[]>([]);
  filteredBooks = signal<SingleBook[]>([]);
  isLoadingBooks = signal(false);

  showQrScanner = signal(false);

  isProcessing = signal(false);
  actionMessage = signal<{ type: 'success' | 'error'; text: string } | null>(null);

  private userSearchSubject = new Subject<string>();
  private bookSearchSubject = new Subject<string>();

  constructor() {
    this.userSearchSubject.pipe(debounceTime(400)).subscribe((query) => {
      if (query.trim()) {
        this.searchUser(query.trim());
      }
    });

    this.bookSearchSubject.pipe(debounceTime(300)).subscribe((query) => {
      this.filterBooks(query);
    });
  }

  ngOnInit() {
    this.loadAvailableBooks();
  }

  onUserSearchChange() {
    this.userSearchSubject.next(this.userSearchQuery);
  }

  searchUser(query: string) {
    this.isSearchingUser.set(true);
    this.userSearchError.set(null);

    this.userService
      .findByUsername(query)
      .pipe(
        catchError(() => this.userService.findByEmail(query)),
        catchError(() => {
          // Try parsing as ID
          const id = parseInt(query, 10);
          if (!isNaN(id)) {
            return this.userService.findById(id);
          }
          return of(null);
        }),
      )
      .subscribe({
        next: (user) => {
          this.isSearchingUser.set(false);
          if (user) {
            this.selectUser(user);
          } else {
            this.userSearchError.set('Nie znaleziono użytkownika');
            this.selectedUser.set(null);
            this.userLoans.set([]);
          }
        },
        error: () => {
          this.isSearchingUser.set(false);
          this.userSearchError.set('Błąd podczas wyszukiwania użytkownika');
        },
      });
  }

  selectUser(user: User) {
    this.selectedUser.set(user);
    this.userSearchError.set(null);
    this.loadUserLoans(user.id);
  }

  loadUserLoans(userId: number) {
    this.loanService.getUserLoans(userId).subscribe({
      next: (loans) => this.userLoans.set(loans),
      error: () => this.userLoans.set([]),
    });
  }

  clearUser() {
    this.selectedUser.set(null);
    this.userLoans.set([]);
    this.userSearchQuery = '';
    this.userSearchError.set(null);
  }

  openQrScanner() {
    this.showQrScanner.set(true);
  }

  closeQrScanner() {
    this.showQrScanner.set(false);
  }

  onQrCodeScanned(readerId: string) {
    this.closeQrScanner();
    const id = parseInt(readerId, 10);
    if (!isNaN(id)) {
      this.isSearchingUser.set(true);
      this.userService.findById(id).subscribe({
        next: (user) => {
          this.isSearchingUser.set(false);
          this.selectUser(user);
          this.userSearchQuery = user.email;
        },
        error: () => {
          this.isSearchingUser.set(false);
          this.userSearchError.set('Nie znaleziono użytkownika o podanym ID');
        },
      });
    } else {
      this.userSearchError.set('Nieprawidłowy kod QR');
    }
  }

  loadAvailableBooks() {
    this.isLoadingBooks.set(true);
    this.loanService.getAvailableItems().subscribe({
      next: (books) => {
        this.availableBooks.set(books);
        this.filteredBooks.set(books);
        this.isLoadingBooks.set(false);
      },
      error: () => {
        this.isLoadingBooks.set(false);
      },
    });
  }

  onBookSearchChange() {
    this.bookSearchSubject.next(this.bookSearchQuery);
  }

  filterBooks(query: string) {
    const lowerQuery = query.toLowerCase().trim();
    if (!lowerQuery) {
      this.filteredBooks.set(this.availableBooks());
      return;
    }

    const filtered = this.availableBooks().filter(
      (book) =>
        book.title.toLowerCase().includes(lowerQuery) ||
        book.author.toLowerCase().includes(lowerQuery) ||
        book.isbn?.toLowerCase().includes(lowerQuery),
    );
    this.filteredBooks.set(filtered);
  }

  rentBook(book: SingleBook) {
    const user = this.selectedUser();
    if (!user) {
      this.showMessage('error', 'Najpierw wybierz użytkownika');
      return;
    }

    this.isProcessing.set(true);
    this.loanService.rentItem({ libraryItemId: book.id, userId: user.id }).subscribe({
      next: () => {
        this.isProcessing.set(false);
        this.showMessage('success', `Książka "${book.title}" została wypożyczona`);
        this.loadAvailableBooks();
        this.loadUserLoans(user.id);
      },
      error: (err) => {
        this.isProcessing.set(false);
        this.showMessage('error', err.error?.message || 'Błąd podczas wypożyczania');
      },
    });
  }

  returnBook(book: SingleBook) {
    this.isProcessing.set(true);
    this.loanService.returnItem(book.id).subscribe({
      next: () => {
        this.isProcessing.set(false);
        this.showMessage('success', `Książka "${book.title}" została zwrócona`);
        const user = this.selectedUser();
        if (user) {
          this.loadUserLoans(user.id);
        }
        this.loadAvailableBooks();
      },
      error: (err) => {
        this.isProcessing.set(false);
        this.showMessage('error', err.error?.message || 'Błąd podczas zwracania');
      },
    });
  }

  extendLoan(book: SingleBook) {
    this.isProcessing.set(true);
    this.loanService.extendLoan(book.id, 7).subscribe({
      next: () => {
        this.isProcessing.set(false);
        this.showMessage('success', `Termin zwrotu książki "${book.title}" przedłużony o 7 dni`);
        const user = this.selectedUser();
        if (user) {
          this.loadUserLoans(user.id);
        }
      },
      error: (err) => {
        this.isProcessing.set(false);
        this.showMessage('error', err.error?.message || 'Błąd podczas przedłużania');
      },
    });
  }

  // ========== HELPERS ==========

  showMessage(type: 'success' | 'error', text: string) {
    this.actionMessage.set({ type, text });
    setTimeout(() => this.actionMessage.set(null), 4000);
  }

  formatDate(dateString?: string): string {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString('pl-PL', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
    });
  }

  isOverdue(dueDate?: string): boolean {
    if (!dueDate) return false;
    return new Date(dueDate) < new Date();
  }
}
