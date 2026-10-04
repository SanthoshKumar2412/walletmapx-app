
import {
  Component,
  inject
} from '@angular/core';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import {
  Router,
  RouterLink
} from '@angular/router';

import {
  Auth,
  RegisterRequest,
  ApiResponse
} from '../../../core/services/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './register.html'
})
export class Register {

  private readonly fb =
    inject(FormBuilder);

  private readonly authService =
    inject(Auth);

  private readonly router =
    inject(Router);

  // =========================================================
  // FORM
  // =========================================================

  registerForm =
    this.fb.nonNullable.group({

      name: [
        '',
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(100)
        ]
      ],

      email: [
        '',
        [
          Validators.required,
          Validators.email
        ]
      ],

      password: [
        '',
        [
          Validators.required,
          Validators.minLength(6),
          Validators.maxLength(100)
        ]
      ]

    });

  loading = false;

  errorMessage = '';

  successMessage = '';

  // =========================================================
  // REGISTER
  // =========================================================

  onSubmit(): void {

    this.errorMessage = '';
    this.successMessage = '';

    if (this.registerForm.invalid) {

      this.registerForm.markAllAsTouched();

      return;
    }

    this.loading = true;

    const request: RegisterRequest =
      this.registerForm.getRawValue();

    this.authService
      .register(request)
      .subscribe({

        next: (response: ApiResponse<string>) => {

          console.log(
            'Registration successful:',
            response
          );

          this.loading = false;

          this.successMessage =
            response.message ||
            'Registration successful.';

          this.registerForm.reset();

          // Redirect to login
          setTimeout(() => {

            this.router.navigate([
              '/login'
            ]);

          }, 1000);
        },

        error: (error: unknown) => {

          console.error(
            'Registration failed:',
            error
          );

          this.loading = false;

          const httpError =
            error as {
              error?: {
                message?: string;
              };
            };

          this.errorMessage =
            httpError?.error?.message ||
            'Registration failed. Please try again.';
        }

      });
  }

  // =========================================================
  // FORM HELPERS
  // =========================================================

  get name() {

    return this.registerForm.controls.name;
  }

  get email() {

    return this.registerForm.controls.email;
  }

  get password() {

    return this.registerForm.controls.password;
  }
}

