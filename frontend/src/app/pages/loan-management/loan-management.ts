import { ChangeDetectionStrategy, Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { LoanService } from '../../services/loan.service';
import { UserService } from '../../services/user.service';
import { SingleBook, User, LibraryBranch } from '../../types';
import { debounceTime, Subject } from 'rxjs';
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
  searchResults = signal<User[]>([]);

  employeeBranch = signal<LibraryBranch | null>(null);
  employeeBranchError = signal<string | null>(null);
  isLoadingBranch = signal(false);

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
    this.loadEmployeeBranch();
  }

  loadEmployeeBranch() {
    this.isLoadingBranch.set(true);
    this.employeeBranchError.set(null);
    this.userService.getEmployeeBranch().subscribe({
      next: (branch) => {
        this.isLoadingBranch.set(false);
        if (branch) {
          this.employeeBranch.set(branch);
        } else {
          this.employeeBranchError.set(
            'Bibliotekarka nie jest obecnie zatrudniona w żadnej bibliotece'
          );
        }
      },
      error: () => {
        this.isLoadingBranch.set(false);
        this.employeeBranchError.set(
          'Bibliotekarka nie jest obecnie zatrudniona w żadnej bibliotece'
        );
      },
    });
  }

  onUserSearchChange() {
    this.userSearchSubject.next(this.userSearchQuery);
  }

  searchUser(query: string) {
    this.isSearchingUser.set(true);
    this.userSearchError.set(null);
    this.searchResults.set([]);

    this.userService.searchUsers(query).subscribe({
      next: (users) => {
        this.isSearchingUser.set(false);
        if (users.length === 0) {
          this.userSearchError.set('Nie znaleziono użytkownika');
          this.selectedUser.set(null);
          this.userLoans.set([]);
        } else if (users.length === 1) {
          this.selectUser(users[0]);
        } else {
          this.searchResults.set(users);
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
    this.searchResults.set([]);
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
    this.searchResults.set([]);
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
        book.isbn?.toLowerCase().includes(lowerQuery)
    );
    this.filteredBooks.set(filtered);
  }

  rentBook(book: SingleBook) {
    const user = this.selectedUser();
    const branch = this.employeeBranch();

    if (!user) {
      this.showMessage('error', 'Najpierw wybierz użytkownika');
      return;
    }

    if (!branch) {
      this.showMessage('error', 'Bibliotekarka nie jest obecnie zatrudniona w żadnej bibliotece');
      return;
    }

    this.isProcessing.set(true);
    this.loanService
      .rentItem({ libraryItemId: book.id, userId: user.id, branchId: branch.id })
      .subscribe({
        next: () => {
          this.isProcessing.set(false);
          this.showMessage('success', `Książka "${book.title}" została wypożyczona`);
          this.loadAvailableBooks();
          this.loadUserLoans(user.id);
        },
        error: (err) => {
          this.isProcessing.set(false);
          const errorMessage = this.extractErrorMessage(err);
          this.showMessage('error', errorMessage);
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


  extractErrorMessage(err: any): string {
    if (err.error?.errors?.error && Array.isArray(err.error.errors.error)) {
      const errorMessages = err.error.errors.error;
      if (errorMessages.includes('User cannot rent more items')) {
        return 'Użytkownik nie może wypożyczyć więcej książek';
      }
      return errorMessages.join('. ');
    }
    if (err.error?.message && err.error.message !== 'An unexpected error occurred') {
      return err.error.message;
    }
    return 'Błąd podczas wypożyczania';
  }

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
