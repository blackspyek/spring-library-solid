import { Component, Inject, input, PLATFORM_ID, OnInit } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
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
export class Navbar implements OnInit {
  currentPage = input<string>('Start');

  // High contrast state
  highContrast = false;

  private currentScale = 100; // w procentach
  private minScale = 50;
  private maxScale = 200;

  platformId = Inject(PLATFORM_ID);
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

  private getCurrentRootFontSize(): number {
    if (isPlatformBrowser(this.platformId)) {
      const rootElement = document.documentElement;
      const currentSize = parseFloat(
        rootElement.style.fontSize || getComputedStyle(rootElement).fontSize,
      );
      return isNaN(currentSize) ? 16 : currentSize;
    }
    return 16;
  }

  ngOnInit(): void {
    console.log(this.currentPage);
    try {
      const stored = localStorage.getItem('highContrast');
      this.highContrast = stored === 'true';
      this.applyHighContrast(this.highContrast);
    } catch (e) {
      // ignore during SSR or if storage is unavailable
    }
  }

  toggleHighContrast(): void {
    this.highContrast = !this.highContrast;
    try {
      localStorage.setItem('highContrast', String(this.highContrast));
    } catch (e) {}
    this.applyHighContrast(this.highContrast);
  }

  private applyHighContrast(enabled: boolean): void {
    try {
      const root = document.documentElement;
      if (enabled) root.classList.add('high-contrast');
      else root.classList.remove('high-contrast');
    } catch (e) {
      // ignore on server
    }
  }
  /**
   * Increases the root font size, scaling all REM-based text up.
   */
  increaseRootFontSize(): void {
    if (this.currentScale < this.maxScale) {
      this.currentScale += 12.5;
      document.documentElement.style.fontSize = `${this.currentScale}%`;
    }
  }

  decreaseRootFontSize(): void {
    if (this.currentScale > this.minScale) {
      this.currentScale -= 12.5;
      document.documentElement.style.fontSize = `${this.currentScale}%`;
    }
  }
}
