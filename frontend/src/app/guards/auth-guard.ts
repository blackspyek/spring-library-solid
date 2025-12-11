import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth-service';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Wait for auth to be initialized
  if (!authService.isInitialized()) {
    // If not initialized yet, deny access (APP_INITIALIZER should handle this)
    void router.navigate(['/zaloguj-sie']);
    return false;
  }

  if (authService.isLoggedIn()) {
    return true;
  }

  void router.navigate(['/zaloguj-sie']);
  return false;
};
