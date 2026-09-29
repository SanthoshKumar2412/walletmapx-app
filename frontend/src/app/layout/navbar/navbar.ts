import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';

import {
  ProfileResponse,
  ProfileService
} from '../../core/services/profile';

@Component({
  selector: 'app-navbar',
  standalone: true,
  templateUrl: './navbar.html'
})
export class Navbar implements OnInit {

  @Output() menuClicked =
    new EventEmitter<void>();

  profile: ProfileResponse | null = null;

  readonly backendUrl =
    'http://localhost:8082';

  constructor(
    private router: Router,
    private profileService: ProfileService
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  // =========================================================
  // LOAD PROFILE
  // =========================================================

  private loadProfile(): void {

    this.profileService.getMyProfile().subscribe({

      next: (response) => {

        this.profile = response;

        // Keep latest user information
        localStorage.setItem(
          'user',
          JSON.stringify(response)
        );
      },

      error: (error) => {

        console.error(
          'Navbar profile loading failed:',
          error
        );

      }

    });
  }

  // =========================================================
  // PROFILE IMAGE URL
  // =========================================================

  get profileImageUrl(): string | null {

    if (!this.profile?.profileImageUrl) {
      return null;
    }

    return this.backendUrl +
      this.profile.profileImageUrl;
  }

  // =========================================================
  // PROFILE ICON CLICK
  // =========================================================

  openProfile(): void {

    this.router.navigate(['/profile']);
  }

  // =========================================================
  // MENU
  // =========================================================

  onMenuClick(): void {

    this.menuClicked.emit();
  }

  // =========================================================
  // LOGOUT
  // =========================================================

  logout(): void {

    localStorage.removeItem('token');
    localStorage.removeItem('user');

    this.router.navigate(['/login']);
  }
}