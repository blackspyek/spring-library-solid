import { Component, computed, inject } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NgOptimizedImage } from '@angular/common';
import { AuthService } from '../../services/auth-service';

export interface NavItem {
  label: string;
  icon?: string;
  link?: string;
}
@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [MatIcon, RouterLink, RouterLinkActive, NgOptimizedImage],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class Navbar {
  private authService = inject(AuthService);

  isLoggedIn = this.authService.isLoggedIn;

  mobileNavItems = computed<NavItem[]>(() => [
    { label: 'Start', icon: 'home', link: '/' },
    { label: 'Katalog', icon: 'list', link: '/katalog' },
    { label: 'Moja karta', icon: 'none', link: '/moja-karta' },
    {
      label: this.isLoggedIn() ? 'Konto' : 'Zaloguj',
      icon: 'person',
      link: this.isLoggedIn() ? '/profil' : '/zaloguj-sie',
    },
    { label: '', icon: '' },
  ]);

  desktopNavItems = [
    { label: 'Strona główna', link: '/' },
    { label: 'Katalog', link: '/catalog' },
  ];

  logout(): void {
    this.authService.logout();
  }
}
