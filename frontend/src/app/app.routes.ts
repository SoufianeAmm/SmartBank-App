import { Routes } from '@angular/router';

import { roleGuard } from './core/auth/role.guard';

export const routes: Routes = [
  {
    path: '',
    canActivate: [roleGuard],
    loadComponent: () => import('./features/home/home').then((m) => m.Home),
  },
  // Exemples de routes à créer par chaque équipe :
  // { path: 'accounts',  canActivate: [roleGuard], data: { roles: ['CLIENT'] }, loadComponent: ... },
  // { path: 'customers', canActivate: [roleGuard], data: { roles: ['AGENT', 'ADMIN'] }, loadComponent: ... },
  {
    path: 'forbidden',
    loadComponent: () => import('./features/forbidden/forbidden').then((m) => m.Forbidden),
  },
  { path: '**', redirectTo: '' },
];
