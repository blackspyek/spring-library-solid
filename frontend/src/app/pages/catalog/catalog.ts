import { ChangeDetectionStrategy, Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

interface Book {
  id: number;
  title: string;
  author: string;
  coverImage: string;
  hasDetails: boolean;
  hasAvailability: boolean;
}

@Component({
selector: 'catalog',
imports: [CommonModule, RouterLink],
templateUrl: './catalog.html',
styleUrl: './catalog.scss',
changeDetection: ChangeDetectionStrategy.OnPush,
})

export class Catalog {
  books = signal<Book[]>([]);

  loadBooks(): void {
    // Tu bedzie backend, ale nie dziala
    this.books.set([
      {
        id: 1,
        title: 'Tajemnica Domu Uklęjon',
        author: 'Anna Jakowska',
        coverImage: '',
        hasDetails: true,
        hasAvailability: true
      },
      {
        id: 2,
        title: 'Cienie Pośród Mroku',
        author: 'Zobaczyć Kult',
        coverImage: '',
        hasDetails: true,
        hasAvailability: true
      }
    ]);
  }

}
