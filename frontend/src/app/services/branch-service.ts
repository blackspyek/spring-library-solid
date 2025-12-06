import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LibraryBranch } from '../types';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class BranchService {
  private http = inject(HttpClient);
  private API_URL = environment.apiUrl + 'branches';

  private branchesSignal = signal<LibraryBranch[]>([]);
  public branches = this.branchesSignal.asReadonly();

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
