import { Component, signal } from '@angular/core';
import { NgOptimizedImage } from '@angular/common';
import { Book } from '../../types';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-popular-list',
  imports: [NgOptimizedImage, MatIcon],
  templateUrl: './popular-list.html',
  styleUrl: './popular-list.scss',
})
export class PopularList {
  popularBooks = signal<Book[]>([
    {
      id: 1,
      title: 'Langer',
      author: 'Remigiusz Mróz',
      coverUrl: 'https://placehold.co/224x336/a00000/ffffff?text=Langer',
    },
    {
      id: 2,
      title: 'Z pierwszej piłki',
      author: 'Remigiusz Mróz',
      coverUrl: 'https://placehold.co/224x336/d16000/ffffff?text=Z+pierwszej+pi%C5%82ki',
    },
    {
      id: 3,
      title: 'W głębi',
      author: 'Katarzyna Bonda',
      coverUrl: 'https://placehold.co/224x336/004225/ffffff?text=W+g%C5%82%C4%99bi',
    },
    {
      id: 4,
      title: 'Tajemnica Domu Uklejów',
      author: 'Areta Jagodzińska',
      coverUrl: 'https://placehold.co/224x336/4b0082/ffffff?text=Tajemnica',
    },
    {
      id: 5,
      title: 'Projekt Riese',
      author: 'Remigiusz Mróz',
      coverUrl: 'https://placehold.co/224x336/36454f/ffffff?text=Projekt+Riese',
    },
    {
      id: 6,
      title: 'Behawiorysta',
      author: 'Remigiusz Mróz',
      coverUrl: 'https://placehold.co/224x336/222222/ffffff?text=Behawiorysta',
    },
    {
      id: 7,
      title: 'Behawiorysta',
      author: 'Remigiusz Mróz',
      coverUrl: 'https://placehold.co/224x336/222222/ffffff?text=Behawiorysta',
    },
    {
      id: 8,
      title: 'Behawiorysta',
      author: 'Remigiusz Mróz',
      coverUrl: 'https://placehold.co/224x336/222222/ffffff?text=Behawiorysta',
    },
    {
      id: 9,
      title: 'Behawiorysta',
      author: 'Remigiusz Mróz',
      coverUrl: 'https://placehold.co/224x336/222222/ffffff?text=Behawiorysta',
    },
  ]);

  scrollLeft(element: HTMLElement): void {
    const scrollAmount = element.clientWidth * 0.8;
    element.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
  }

  scrollRight(element: HTMLElement): void {
    const scrollAmount = element.clientWidth * 0.8;
    element.scrollBy({ left: scrollAmount, behavior: 'smooth' });
  }
}
