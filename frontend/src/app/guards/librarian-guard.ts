import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth-service';
import { Role } from '../types';

export const librarianGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    void router.navigate(['/zaloguj-sie']);
    return false;
  }

  const roles = authService.getUserRoles();
  const hasLibrarianAccess = roles?.some(
    (role) => role === Role.ROLE_LIBRARIAN || role === Role.ROLE_ADMIN,
  );

  if (hasLibrarianAccess) {
    return true;
  }

  void router.navigate(['/']);
  return false;
};
