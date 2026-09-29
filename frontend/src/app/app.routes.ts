import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth-guard';
import { Layout } from './layout/layout';

export const routes: Routes = [

  // =========================================================
  // AUTH
  // =========================================================

  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login')
        .then(m => m.Login)
  },

  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register')
        .then(m => m.Register)
  },

  // =========================================================
  // PROTECTED APPLICATION
  // =========================================================

  {
    path: '',
    component: Layout,
    canActivate: [authGuard],

    children: [

      // Dashboard
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard')
            .then(m => m.Dashboard)
      },

      // Income
      {
        path: 'income',
        loadComponent: () =>
          import('./features/income/income')
            .then(m => m.Income)
      },

      // Expenses
      {
        path: 'expenses',
        loadComponent: () =>
          import('./features/expenses/expenses')
            .then(m => m.Expenses)
      },

      // Assets
      {
        path: 'assets',
        loadComponent: () =>
          import('./features/assets/assets')
            .then(m => m.Assets)
      },

      // Liabilities
      {
        path: 'liabilities',
        loadComponent: () =>
          import('./features/liabilities/liabilities')
            .then(m => m.Liabilities)
      },

      // Investments
      {
        path: 'investments',
        loadComponent: () =>
          import('./features/investments/investments')
            .then(m => m.Investments)
      },

      // Statistics
      {
        path: 'statistics',
        loadComponent: () =>
          import('./features/statistics/statistics')
            .then(m => m.Statistics)
      },

      // Profile
      {
        path: 'profile',
        loadComponent: () =>
          import('./features/profile/profile')
            .then(m => m.Profile)
      },

      // Empty protected path → Dashboard
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  },

  // =========================================================
  // UNKNOWN ROUTE
  // =========================================================

  {
    path: '**',
    redirectTo: 'login'
  }
];