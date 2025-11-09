import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth-service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  console.log('test');
  if (authService.isLoggedIn()) return true;
  void router.navigate(['/zaloguj-sie']);
  return false;
};
