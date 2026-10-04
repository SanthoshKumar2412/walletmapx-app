import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

function isExpired(token: string): boolean {
  try {
    const payload = token.split('.')[1];
    const json = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    const { exp } = JSON.parse(json) as { exp?: number };

    return !!exp && exp * 1000 <= Date.now();
  } catch {
    // Malformed token: treat as invalid
    return true;
  }
}

export const authGuard: CanActivateFn = () => {

  const token = localStorage.getItem('token');

  if (token && !isExpired(token)) {
    return true;
  }

  localStorage.removeItem('token');
  localStorage.removeItem('user');

  return inject(Router).createUrlTree(['/login']);
};