import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs';

import {
  ProfileResponse,
  ProfileService
} from '../../core/services/profile';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-profile',
  standalone: true,
  templateUrl: './profile.html'
})
export class Profile implements OnInit {
  readonly backendUrl = environment.apiBaseUrl;
  profile: ProfileResponse | null = null;

  loading = true;
  errorMessage = '';

  selectedFile: File | null = null;

  uploading = false;
  uploadMessage = '';

  constructor(
    private profileService: ProfileService
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  // =========================================================
  // LOAD PROFILE
  // =========================================================

  private loadProfile(): void {

    this.loading = true;
    this.errorMessage = '';

    this.profileService
      .getMyProfile()
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({

        next: (response) => {

          console.log(
            'Profile loaded:',
            response
          );

          this.profile = response;

          // Keep latest profile locally
          localStorage.setItem(
            'user',
            JSON.stringify(response)
          );
        },

        error: (error) => {

          console.error(
            'Profile loading failed:',
            error
          );

          this.errorMessage =
            error?.error?.message ||
            'Unable to load profile.';
        }

      });
  }

  // =========================================================
  // SELECT IMAGE
  // =========================================================

  onFileSelected(event: Event): void {

    const input =
      event.target as HTMLInputElement;

    if (
      !input.files ||
      input.files.length === 0
    ) {
      return;
    }

    this.selectedFile =
      input.files[0];

    this.uploadMessage = '';
  }

  // =========================================================
  // UPLOAD IMAGE
  // =========================================================

  uploadImage(): void {

    if (!this.selectedFile) {

      this.uploadMessage =
        'Please select an image.';

      return;
    }

    this.uploading = true;
    this.uploadMessage = '';

    this.profileService
      .uploadProfileImage(this.selectedFile)
      .pipe(
        finalize(() => {
          this.uploading = false;
        })
      )
      .subscribe({

        next: (response) => {

          console.log(
            'Profile image updated:',
            response
          );

          // Immediately update screen
          this.profile = response;

          // Keep latest profile
          localStorage.setItem(
            'user',
            JSON.stringify(response)
          );

          this.selectedFile = null;

          this.uploadMessage =
            'Profile image updated successfully.';
        },

        error: (error) => {

          console.error(
            'Profile image upload failed:',
            error
          );

          this.uploadMessage =
            error?.error?.message ||
            'Failed to upload profile image.';
        }

      });
  }
}