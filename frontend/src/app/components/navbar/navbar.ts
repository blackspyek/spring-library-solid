import { Component, input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
export interface NavItem {
  label: string;
  icon?: string;
  link?: string;
}
@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [MatIcon, RouterLink],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class Navbar {
  currentPage = input<string>('Start');

  mobileNavItems: NavItem[] = [
    { label: 'Start', icon: 'home', link: '/' },
    { label: 'Katalog', icon: 'list', link: '/katalog' },
    { label: 'Moja karta', icon: 'none', link: '/moja-karta' },
    { label: 'Konto', icon: 'person', link: '/profil' },

    { label: '', icon: '' },
  ];

  desktopNavItems = [
    { label: 'Strona główna', link: '/home' },
    { label: 'Katalog', link: '/catalog' },
  ];
}
