import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import {
  ReactiveFormsModule,
  FormGroup,
  FormBuilder,
  Validators,
  ValidationErrors,
  AbstractControl,
} from '@angular/forms';
import { finalize } from 'rxjs';
import { ChangePasswordRequest, UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';

function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const newPassword = control.get('newPassword');
  const confirmPassword = control.get('confirmPassword');

  if (newPassword?.value && confirmPassword?.value && newPassword.value !== confirmPassword.value) {
    return { mismatch: true };
  }
  return null;
}

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './change-password.html',
  styleUrl: './change-password.scss',
})
export class ChangePassword {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private userService = inject(UserService);
  private authService = inject(AuthService);

  passwordForm: FormGroup;
  submitted = false;
  loading = false;
  saveError = signal('');
  saveSuccess = signal(false);

  constructor() {
    this.passwordForm = this.fb.group(
      {
        currentPassword: ['', Validators.required],
        newPassword: ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', Validators.required],
      },
      {
        validators: passwordMatchValidator,
      },
    );
  }

  onSaveClick(): void {
    this.submitted = true;
    this.saveError.set('');
    this.saveSuccess.set(false);
    this.passwordForm.markAllAsTouched();

    if (this.passwordForm.valid && !this.loading) {
      this.loading = true;

      const formValue = this.passwordForm.value;

      const request: ChangePasswordRequest = {
        oldPassword: formValue.currentPassword,
        newPassword: formValue.newPassword,
      };

      this.userService
        .changePassword(request)
        .pipe(finalize(() => (this.loading = false)))
        .subscribe({
          next: () => {
            this.saveSuccess.set(true);
            // Update auth state to reflect password change
            this.authService.clearMustChangePassword();
            // Redirect to profile after short delay
            setTimeout(() => {
              void this.router.navigate(['/profil']);
            }, 1500);
          },
          error: (error) => {
            console.error('Błąd zmiany hasła:', error);

            let errorMessage = 'Nieoczekiwany błąd serwera. Spróbuj ponownie później.';

            if (error.error) {
              const errorBody = error.error;

              if (
                errorBody.errors &&
                Array.isArray(errorBody.errors.error) &&
                errorBody.errors.error.length > 0
              ) {
                errorMessage = errorBody.errors.error[0];
              } else if (typeof errorBody === 'string') {
                errorMessage = errorBody;
              } else if (typeof errorBody.error === 'string') {
                errorMessage = errorBody.error;
              } else if (errorBody.message) {
                errorMessage = errorBody.message;
              }
            }

            this.saveError.set(errorMessage);
          },
        });
    }
  }
}
