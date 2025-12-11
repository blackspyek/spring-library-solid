import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth-service';
import { NgOptimizedImage } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  emailDomainValidator,
  applySuggestedEmail,
  EmailDomainError,
} from '../../validators/email-domain.validator';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink, NgOptimizedImage],
  templateUrl: './login.html',
  styleUrl: './login.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Login {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);

  errorMessage = signal<string | null>(null);
  isLoading = signal<boolean>(false);

  loginForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email, emailDomainValidator()]],
    password: ['', Validators.required],
  });

  onSubmit() {
    this.loginForm.markAllAsTouched();
    this.errorMessage.set(null);

    if (this.loginForm.valid) {
      this.isLoading.set(true);

      this.authService.login(this.loginForm.value).subscribe({
        next: () => {
          this.isLoading.set(false);
        },
        error: (error: HttpErrorResponse) => {
          this.isLoading.set(false);
          this.errorMessage.set(this.getErrorMessage(error));
        },
      });
    }
  }

  applySuggestion(): void {
    applySuggestedEmail(this.loginForm.get('email')!);
    this.errorMessage.set(null);
  }

  get emailSuggestion(): EmailDomainError | null {
    const errors = this.email?.errors;
    return errors?.['emailDomainSuggestion'] || null;
  }

  private getErrorMessage(error: HttpErrorResponse): string {
    switch (error.status) {
      case 400:
        return 'Nieprawidłowe dane logowania. Sprawdź poprawność formularza.';
      case 401:
        return 'Nieprawidłowy email lub hasło.';
      case 403:
        return 'Konto nie zostało jeszcze aktywowane. Sprawdź swoją skrzynkę email.';
      case 404:
        return 'Nieprawidłowy email lub hasło.';
      case 429:
        return 'Zbyt wiele prób logowania. Spróbuj ponownie za kilka minut.';
      case 500:
        return 'Wystąpił błąd serwera. Spróbuj ponownie później.';
      case 0:
        return 'Brak połączenia z serwerem. Sprawdź swoje połączenie internetowe.';
      default:
        return 'Wystąpił nieoczekiwany błąd. Spróbuj ponownie.';
    }
  }

  get email() {
    return this.loginForm.get('email');
  }
  get password() {
    return this.loginForm.get('password');
  }
}
