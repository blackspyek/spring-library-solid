import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, tap } from 'rxjs';
import { LibraryBranch } from '../types';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class BranchService {
  private http = inject(HttpClient);
  private API_URL = environment.apiUrl + 'branches';

  private branchesSignal = signal<LibraryBranch[]>([]);
  private loaded = signal(false);

  /** Read-only signal of all cached branches */
  public branches = this.branchesSignal.asReadonly();

  /**
   * Load branches with caching. Only fetches from API on first call.
   * Use this instead of getAllBranches() when you want caching.
   */
  loadBranches(): Observable<LibraryBranch[]> {
    if (this.loaded()) {
      return of(this.branchesSignal());
    }
    return this.getAllBranches().pipe(tap(() => this.loaded.set(true)));
  }

  /**
   * Get a branch from the store by ID. Returns undefined if not loaded or not found.
   * Make sure to call loadBranches() first.
   */
  getBranchFromStore(id: number): LibraryBranch | undefined {
    return this.branchesSignal().find((b) => b.id === id);
  }

  getAllBranches(): Observable<LibraryBranch[]> {
    return this.http
      .get<LibraryBranch[]>(this.API_URL)
      .pipe(tap((branches) => this.branchesSignal.set(branches)));
  }

  getBranchById(id: number): Observable<LibraryBranch> {
    return this.http.get<LibraryBranch>(`${this.API_URL}/${id}`);
  }

  getBranchByNumber(branchNumber: string): Observable<LibraryBranch> {
    return this.http.get<LibraryBranch>(`${this.API_URL}/number/${branchNumber}`);
  }

  searchBranches(query: string): Observable<LibraryBranch[]> {
    return this.http.get<LibraryBranch[]>(`${this.API_URL}/search`, {
      params: query ? { query } : {},
    });
  }
}

