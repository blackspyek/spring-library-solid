import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { NgOptimizedImage } from '@angular/common';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, NgOptimizedImage],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ForgotPassword {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);

  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  isLoading = signal<boolean>(false);

  forgotPasswordForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    pesel: ['', [Validators.required, Validators.pattern(/^\d{11}$/)]],
  });

  onSubmit() {
    this.forgotPasswordForm.markAllAsTouched();
    this.errorMessage.set(null);
    this.successMessage.set(null);

    if (this.forgotPasswordForm.valid) {
      this.isLoading.set(true);

      const resetData = {
        email: this.forgotPasswordForm.get('email')?.value,
        pesel: this.forgotPasswordForm.get('pesel')?.value,
      };

      this.authService.resetPassword(resetData).subscribe({
        next: (response) => {
          this.isLoading.set(false);
          this.successMessage.set(
            response.message ||
              'Jeśli podane dane są poprawne, nowe hasło zostanie wysłane na podany adres email.',
          );
          this.forgotPasswordForm.reset();
        },
        error: () => {
          this.isLoading.set(false);
          // Always show generic message for security
          this.successMessage.set(
            'Jeśli podane dane są poprawne, nowe hasło zostanie wysłane na podany adres email.',
          );
        },
      });
    }
  }

  get email() {
    return this.forgotPasswordForm.get('email');
  }

  get pesel() {
    return this.forgotPasswordForm.get('pesel');
  }
}
