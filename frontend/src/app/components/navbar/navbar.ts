import { Component, computed, inject, PLATFORM_ID, OnInit } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NgOptimizedImage, isPlatformBrowser, NgClass } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { AuthService } from '../../services/auth.service';
import { FeedbackDialog } from '../feedback-dialog/feedback-dialog';
import { QrDialog } from '../qr-dialog/qr-dialog';
export interface NavItem {
  label: string;
  icon?: string;
  link?: string;
  action?: () => void;
}

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [MatIcon, RouterLink, RouterLinkActive, NgOptimizedImage, NgClass],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class Navbar implements OnInit {
  private authService = inject(AuthService);
  private platformId = inject(PLATFORM_ID);
  private dialog = inject(MatDialog);

  isLoggedIn = this.authService.isLoggedIn;

  highContrast = false;
  showMobileA11yMenu = false;
  private currentScale = 100;
  private minScale = 50;
  private maxScale = 200;

  mobileNavItems = computed<NavItem[]>(() => [
    { label: 'Start', icon: 'home', link: '/' },
    { label: 'Katalog', icon: 'list', link: '/katalog' },
    { label: '', icon: '', link: '' },
    {
      label: this.isLoggedIn() ? 'Konto' : 'Zaloguj',
      icon: 'person',
      link: this.isLoggedIn() ? '/profil' : '/zaloguj-sie',
    },
    {
      label: 'Dostępność',
      icon: 'accessibility_new',
      action: () => this.toggleMobileA11yMenu()
    },
  ]);

  desktopNavItems = [
    { label: 'Strona główna', link: '/' },
    { label: 'Katalog', link: '/katalog' },
  ];

  openQrDialog(): void {
    this.dialog.open(QrDialog, {
      panelClass: 'qr-dialog-container',
      autoFocus: false,
      width: '90vw',
      maxWidth: '400px'
    });
  }

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      try {
        const stored = localStorage.getItem('highContrast');
        this.highContrast = stored === 'true';
        this.applyHighContrast(this.highContrast);
      } catch (e) {
        console.warn('LocalStorage niedostępny', e);
      }
    }
  }

  logout(): void {
    this.authService.logout();
  }

  toggleMobileA11yMenu(): void {
    this.showMobileA11yMenu = !this.showMobileA11yMenu;
  }

  toggleHighContrast(): void {
    this.highContrast = !this.highContrast;
    if (isPlatformBrowser(this.platformId)) {
      try {
        localStorage.setItem('highContrast', String(this.highContrast));
      } catch (e) {
        console.warn('LocalStorage niedostępny', e);
      }
      this.applyHighContrast(this.highContrast);
    }
  }

  private applyHighContrast(enabled: boolean): void {
    if (isPlatformBrowser(this.platformId)) {
      const root = document.documentElement;
      if (enabled) {
        root.classList.add('high-contrast');
      } else {
        root.classList.remove('high-contrast');
      }
    }
  }

  increaseRootFontSize(): void {
    if (this.currentScale < this.maxScale) {
      this.currentScale += 12.5;
      this.updateFontSize();
    }
  }

  decreaseRootFontSize(): void {
    if (this.currentScale > this.minScale) {
      this.currentScale -= 12.5;
      this.updateFontSize();
    }
  }

  private updateFontSize(): void {
    if (isPlatformBrowser(this.platformId)) {
      document.documentElement.style.fontSize = `${this.currentScale}%`;
    }
  }

  isLibrarian = computed(() => this.authService.isLibrarian());

  openFeedbackDialog(): void {
    this.showMobileA11yMenu = false;
    this.dialog.open(FeedbackDialog, {
      panelClass: 'feedback-dialog',
      autoFocus: 'first-tabbable',
      restoreFocus: true,
    });
  }
}
