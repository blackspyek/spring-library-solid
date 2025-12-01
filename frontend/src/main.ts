import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';

// Apply saved high-contrast preference synchronously before bootstrap to avoid FOUC
try {
  const stored = localStorage.getItem('highContrast');
  if (stored === 'true') {
    document.documentElement.classList.add('high-contrast');
  } else if (stored === 'false') {
    document.documentElement.classList.remove('high-contrast');
  }
} catch (e) {
  // ignore on server or if storage is unavailable
}

bootstrapApplication(App, appConfig).catch((err) => console.error(err));
