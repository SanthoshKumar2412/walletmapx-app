import { Component, HostListener, OnDestroy } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { Navbar } from './navbar/navbar';
import { Sidebar } from './sidebar/sidebar';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    Navbar,
    Sidebar
  ],
  templateUrl: './layout.html'
})
export class Layout implements OnDestroy {

  sidebarOpen = false;

  toggleSidebar(): void {
    this.setSidebar(!this.sidebarOpen);
  }

  closeSidebar(): void {
    this.setSidebar(false);
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    this.closeSidebar();
  }

  // Resized/rotated to a desktop width: drop the mobile overlay state
  @HostListener('window:resize')
  onResize(): void {
    if (this.sidebarOpen && window.innerWidth >= 1024) {
      this.closeSidebar();
    }
  }

  ngOnDestroy(): void {
    document.body.classList.remove('overflow-hidden');
  }

  private setSidebar(open: boolean): void {
    this.sidebarOpen = open;
    document.body.classList.toggle('overflow-hidden', open);
  }
}