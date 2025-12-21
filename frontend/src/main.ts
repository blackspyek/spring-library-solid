import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';

try {
  const stored = localStorage.getItem('highContrast');
  if (stored === 'true') {
    document.documentElement.classList.add('high-contrast');
  } else if (stored === 'false') {
    document.documentElement.classList.remove('high-contrast');
  }
} catch (e) {
}

bootstrapApplication(App, appConfig).catch((err) => console.error(err));
