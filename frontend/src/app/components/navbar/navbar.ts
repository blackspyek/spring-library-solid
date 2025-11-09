import { Component, input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [MatIcon],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class Navbar {
  currentPage = input<string>('Start');

  mobileNavItems = [
    { label: 'Start', icon: 'home' },
    { label: 'Katalog', icon: 'list' },
    { label: 'Moja karta', icon: 'none' },
    { label: 'Konto', icon: 'person' },
    { label: 'Zgłoś', icon: 'report' },
  ];

  desktopNavItems = [
    { label: 'Strona główna', link: '/home' },
    { label: 'Katalog', link: '/catalog' },
  ];
}
