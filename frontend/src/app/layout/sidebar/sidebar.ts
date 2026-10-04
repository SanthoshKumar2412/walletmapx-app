import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

import { Auth } from '../../core/services/auth';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './sidebar.html'
})
export class Sidebar {

  @Input() open = false;
  @Output() menuItemClicked = new EventEmitter<void>();

  private auth = inject(Auth);
  private router = inject(Router);

  closeMenu(): void { this.menuItemClicked.emit(); }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}