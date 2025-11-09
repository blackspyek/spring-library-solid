import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Layout } from './components/layout/layout';
import { ResendEmail } from './pages/resend-email/resend-email';
import { Profile } from './pages/profile/profile';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
  {
    path: '',
    component: Layout,
    children: [
      {
        path: 'zaloguj-sie',
        component: Login,
      },
      {
        path: 'zweryfikuj-email',
        component: ResendEmail,
      },
      {
        path: 'profil',
        component: Profile,
        canActivate: [authGuard],
      },
    ],
  },
];
