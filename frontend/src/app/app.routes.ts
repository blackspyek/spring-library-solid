import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Catalog } from './pages/catalog/catalog';
import { Layout } from './components/layout/layout';
import { ResendEmail } from './pages/resend-email/resend-email';
import { Profile } from './pages/profile/profile';
import { authGuard } from './guards/auth-guard';
import { Home } from './pages/home/home';
import { LibraryTest } from './pages/library-test/library-test';

export const routes: Routes = [
  {
    path: '',
    component: Layout,
    children: [
      {
        path: '',
        component: Home,
      },
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
      {
        path: 'catalog',
        component: Catalog,
      },
      {
        path: 'test-library',
        component: LibraryTest,
      },
    ],
  },
];
