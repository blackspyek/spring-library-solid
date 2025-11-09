import { Routes } from '@angular/router';
import { App } from './app';
import { Login } from './pages/login/login';
import { Layout } from './components/layout/layout';

export const routes: Routes = [
  {
    path: '',
    component: Layout,
    children: [
      {
        path: 'login',
        component: Login,
      },
    ],
  },
];
