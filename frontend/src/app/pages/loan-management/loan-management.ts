import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  signal,
  inject,
  computed,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { LoanService } from '../../services/loan.service';
import { UserService, CreateUserRequest } from '../../services/user.service';
import { Role, SingleBook, User, LibraryBranch } from '../../types';
import { debounceTime, Subject } from 'rxjs';
import { QrScannerComponent } from '../../components/qr-scanner/qr-scanner';
import { DeleteUserComponent } from '../../components/delete-user/delete-user';
import { EditUserRoleComponent } from '../../components/edit-user/edit-user';
import { AuthService } from '../../services/auth.service';
import { AddUser } from '../../components/add-user/add-user';
import { BranchService } from '../../services/branch.service';

@Component({
  selector: 'app-loan-management',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatIcon,
    QrScannerComponent,
    EditUserRoleComponent,
    DeleteUserComponent,
    AddUser,
  ],
  templateUrl: './loan-management.html',
  styleUrl: './loan-management.scss',
  changeDetection: ChangeDetectionStrategy.Default,
})
export class LoanManagement implements OnInit {
  private loanService = inject(LoanService);
  private userService = inject(UserService);
  private authService = inject(AuthService);

  isAdmin = computed(() => this.authService.hasRole(Role.ROLE_ADMIN));
  isLibrarian = computed(() => this.authService.isLibrarian());

  employeeBranch = computed(() => this.authService.employeeOfBranch());
  employeeBranchError = computed(() => {
    if (this.isLibrarian() && !this.isAdmin() && !this.employeeBranch()) {
      return 'Bibliotekarka nie jest zatrudniona w żadnej bibliotece';
    }
    return null;
  });
  // Branch data comes from AuthService, so no separate loading needed
  isLoadingBranch = computed(() => !this.authService.isInitialized());

  allUsers = signal<User[]>([]);
  filteredUsers = signal<User[]>([]);
  userSearchQuery = '';
  isLoadingUsers = signal(false);

  selectedUser = signal<User | null>(null);
  userLoans = signal<SingleBook[]>([]);
  isAddingUser = signal(false);

  bookSearchQuery = '';
  availableBooks = signal<SingleBook[]>([]);
  filteredBooks = signal<SingleBook[]>([]);
  isLoadingBooks = signal(false);
  private bookSearchSubject = new Subject<string>();
  private branchService = inject(BranchService);

  showQrScanner = signal(false);
  isProcessing = signal(false);
  actionMessage = signal<{
    type: 'success' | 'error';
    text: string;
    customBg?: string;
    customText?: string;
  } | null>(null);
  showEditRoleModal = signal(false);
  showDeleteConfirmModal = signal(false);

  branchDisplayName = computed(() => {
    const branch = this.employeeBranch();
    if (!branch) return '';
    return `Miejska Biblioteka Publiczna im. H. Łopacińskiego ${branch.name}`;
  });

  constructor() {
    // log from authstate
    console.log(this.authService.getAuthState());
    this.bookSearchSubject.pipe(debounceTime(300)).subscribe((query) => {
      this.filterBooks(query);
    });
  }

  ngOnInit() {
    this.loadAvailableBooks();
    this.loadAllUsers();
    // Employee branch is already available from AuthService.employeeOfBranch()
  }

  getRoleName(roleCtx: any): string {
    if (!roleCtx) return '';
    if (typeof roleCtx === 'object' && 'authority' in roleCtx) {
      return roleCtx.authority;
    }
    return roleCtx as string;
  }

  loadAllUsers() {
    this.isLoadingUsers.set(true);
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.allUsers.set(users);
        this.filterUsers(this.userSearchQuery);
        this.isLoadingUsers.set(false);
      },
      error: () => {
        this.showMessage('error', 'Nie udało się pobrać listy użytkowników');
        this.isLoadingUsers.set(false);
      },
    });
  }

  onUserSearchChange() {
    this.filterUsers(this.userSearchQuery);
  }

  filterUsers(query: string) {
    const q = query.toLowerCase().trim();
    if (!q) {
      this.filteredUsers.set(this.allUsers());
      return;
    }
    const filtered = this.allUsers().filter(
      (u) =>
        (u.email && u.email.toLowerCase().includes(q)) ||
        (u.first_name && u.first_name.toLowerCase().includes(q)) ||
        (u.last_name && u.last_name.toLowerCase().includes(q)) ||
        (u.name && u.name.toLowerCase().includes(q)) || // Obsługa starego pola
        (u.surname && u.surname.toLowerCase().includes(q)) || // Obsługa starego pola
        u.id.toString() === q
    );
    this.filteredUsers.set(filtered);
  }

  selectUser(user: User) {
    this.selectedUser.set(user);
    this.loadUserLoans(user.id);
    this.userSearchQuery = '';
    this.filterUsers('');
  }

  clearUser() {
    this.selectedUser.set(null);
    this.userLoans.set([]);
  }

  loadUserLoans(userId: number) {
    this.loanService.getUserLoans(userId).subscribe({
      next: (loans) => this.userLoans.set(loans),
      error: () => this.userLoans.set([]),
    });
  }

  toggleAddUserForm() {
    this.isAddingUser.update((v) => !v);
  }
  onUserCreated() {
    this.showMessage(
      'success',
      'Użytkownik został pomyślnie utworzony',
      'bg-[color:var(--green-600-to-yellow)]',
      'text-[color:var(--green-800-to-black)]'
    );
    this.isAddingUser.set(false);
    this.loadAllUsers();
  }

  closeAddUser() {
    this.isAddingUser.set(false);
  }

  openEditRoleDialog() {
    if (this.selectedUser()) this.showEditRoleModal.set(true);
  }
  onRoleUpdated() {
    this.showEditRoleModal.set(false);
    this.showMessage('success', 'Rola zaktualizowana');
    this.loadAllUsers();
    if (this.selectedUser()) {
      this.userService.findById(this.selectedUser()!.id).subscribe((u) => this.selectedUser.set(u));
    }
  }

  openDeleteConfirmDialog() {
    if (this.selectedUser()) this.showDeleteConfirmModal.set(true);
  }
  onUserDeleted() {
    this.showDeleteConfirmModal.set(false);
    this.showMessage('success', 'Użytkownik usunięty');
    this.clearUser();
    this.loadAllUsers();
  }

  openQrScanner() {
    this.showQrScanner.set(true);
  }
  closeQrScanner() {
    this.showQrScanner.set(false);
  }
  onQrCodeScanned(readerId: string) {
    this.closeQrScanner();
    // Szukamy lokalnie w załadowanych, jeśli nie ma - dociągamy z API
    const found = this.allUsers().find((u) => u.id.toString() === readerId);
    if (found) {
      this.selectUser(found);
    } else {
      // Fallback do API (jak w V1)
      this.userService.findById(parseInt(readerId)).subscribe({
        next: (u) => this.selectUser(u),
        error: () => this.showMessage('error', 'Brak użytkownika o tym ID'),
      });
    }
  }

  loadAvailableBooks() {
    this.isLoadingBooks.set(true);

    // Check if we should filter by branch
    const branch = this.employeeBranch();
    let request: any;

    if (branch) {
      request = this.loanService.getAvailableItemsByBranch(branch.id);
    } else {
      request = this.loanService.getAvailableItems();
    }

    request.subscribe({
      next: (books: SingleBook[]) => {
        this.availableBooks.set(books);
        this.filteredBooks.set(books);
        this.isLoadingBooks.set(false);
      },
      error: () => this.isLoadingBooks.set(false),
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
      (b) =>
        b.title.toLowerCase().includes(lowerQuery) ||
        b.author.toLowerCase().includes(lowerQuery) ||
        b.isbn?.toLowerCase().includes(lowerQuery)
    );
    this.filteredBooks.set(filtered);
  }

  rentBook(book: SingleBook) {
    const user = this.selectedUser();
    const branch = this.employeeBranch();

    if (!user) {
      this.showMessage('error', 'Wybierz użytkownika');
      return;
    }

    if (!branch && !this.isAdmin()) {
      // Admin może czasem wypożyczyć bez kontekstu filii (zależnie od logiki backendu), ale librarian nie
      this.showMessage('error', 'Błąd: Nie rozpoznano Twojej filii bibliotecznej.');
      return;
    }

    this.isProcessing.set(true);

    const branchId = branch ? branch.id : 0;

    this.loanService.rentItem(book.id, user.id, branchId).subscribe({
      next: () => {
        this.isProcessing.set(false);
        this.showMessage('success', 'Książka wypożyczona');
        this.loadAvailableBooks();
        this.loadUserLoans(user.id);
      },
      error: (err) => {
        this.isProcessing.set(false);
        this.showMessage('error', this.extractErrorMessage(err));
      },
    });
  }

  returnBook(book: SingleBook) {
    const branch = this.employeeBranch();
    const branchId = book.rentedFromBranchId ?? branch?.id;

    if (!branchId) {
      this.showMessage('error', 'Nie można określić filii dla zwrotu');
      return;
    }

    this.isProcessing.set(true);
    this.loanService.returnItem(book.id, branchId).subscribe({
      next: () => {
        this.isProcessing.set(false);
        this.showMessage('success', 'Książka zwrócona');
        if (this.selectedUser()) this.loadUserLoans(this.selectedUser()!.id);
        this.loadAvailableBooks();
      },
      error: (err) => {
        this.isProcessing.set(false);
        this.showMessage('error', this.extractErrorMessage(err));
      },
    });
  }

  extendLoan(book: SingleBook) {
    const branch = this.employeeBranch();
    const branchId = book.rentedFromBranchId ?? branch?.id;

    if (!branchId) {
      this.showMessage('error', 'Nie można określić filii dla przedłużenia');
      return;
    }

    this.isProcessing.set(true);
    this.loanService.extendLoan(book.id, branchId, 7).subscribe({
      next: () => {
        this.isProcessing.set(false);
        this.showMessage('success', 'Przedłużono zwrot o 7 dni');
        if (this.selectedUser()) this.loadUserLoans(this.selectedUser()!.id);
      },
      error: (err) => {
        this.isProcessing.set(false);
        this.showMessage('error', this.extractErrorMessage(err));
      },
    });
  }

  // <--- ZMIANA: Helper do błędów z V1
  extractErrorMessage(err: any): string {
    if (err.error?.errors?.error && Array.isArray(err.error.errors.error)) {
      return err.error.errors.error.join('. ');
    }
    if (err.error?.message) return err.error.message;
    return 'Wystąpił błąd operacji';
  }

  showMessage(type: 'success' | 'error', text: string, customBg?: string, customText?: string) {
    this.actionMessage.set({ type, text, customBg, customText });
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

  getBranchName(branchId: number | undefined): string {
    if (!branchId) return '-';
    // Use BranchService store directly (sync) as branches are loaded on startup
    const branch = this.branchService.getBranchFromStore(branchId);
    return branch ? `${branch.name} (${branch.address})` : `Filia #${branchId}`;
  }

  isOverdue(dueDate?: string): boolean {
    if (!dueDate) return false;
    return new Date(dueDate) < new Date();
  }
}
