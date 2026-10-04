import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';

import { Auth } from '../../core/services/auth';
import {
  ProfileResponse,
  ProfileService
} from '../../core/services/profile';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './navbar.html'
})
export class Navbar implements OnInit, OnDestroy {
  @Input() menuOpen = false;
  @Output() menuClicked = new EventEmitter<void>();

  profile: ProfileResponse | null = null;

  readonly backendUrl = `${environment.apiBaseUrl}`;

  private sub?: Subscription;

  constructor(
    private router: Router,
    private auth: Auth,
    private profileService: ProfileService
  ) {}

  ngOnInit(): void {
    this.sub = this.profileService.profile$.subscribe(p => {
      this.profile = p;
      if (p) {
        localStorage.setItem('user', JSON.stringify(p));
      }
    });

    this.profileService.getMyProfile().subscribe({
      error: (error) =>
        console.error('Navbar profile loading failed:', error)
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  get profileImageUrl(): string | null {
    return this.profile?.profileImageUrl
      ? this.backendUrl + this.profile.profileImageUrl
      : null;
  }

  openProfile(): void {
    this.router.navigate(['/profile']);
  }

  onMenuClick(): void {
    this.menuClicked.emit();
  }

  logout(): void {
    this.auth.logout();
    this.profileService.clear();
    this.router.navigate(['/login']);
  }
}