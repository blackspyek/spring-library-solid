import { Component, inject } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

import {
  ReactiveFormsModule,
  FormGroup,
  FormBuilder,
  Validators,
  ValidationErrors,
  AbstractControl,
} from '@angular/forms';
import { ChangePasswordRequest, UserService } from '../../services/user-service';
import { finalize } from 'rxjs';

function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const newPassword = control.get('newPassword');
  const confirmPassword = control.get('confirmPassword');

  if (newPassword?.value && confirmPassword?.value && newPassword.value !== confirmPassword.value) {
    return { mismatch: true };
  }
  return null;
}

@Component({
  selector: 'app-password-change-dialog',
  standalone: true,
  imports: [MatButtonModule, ReactiveFormsModule],
  templateUrl: './password-change-dialog.html',
  styleUrl: './password-change-dialog.scss',
})
export class PasswordChangeDialog {
  dialogRef = inject<MatDialogRef<PasswordChangeDialog>>(MatDialogRef);
  private fb = inject(FormBuilder);

  private userService = inject(UserService);

  passwordForm: FormGroup;
  submitted = false;

  loading = false;
  saveError: string | null = null;
  saveSuccess: string | null = null;

  constructor() {
    this.passwordForm = this.fb.group(
      {
        currentPassword: ['', Validators.required],
        newPassword: ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', Validators.required],
      },
      {
        validators: passwordMatchValidator,
      }
    );
  }

  onNoClick(): void {
    this.dialogRef.close(null);
  }

  onSaveClick(): void {
    this.submitted = true;
    this.saveError = null;
    this.saveSuccess = null;
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
          next: (response) => {
            this.saveSuccess = response.message || 'Hasło zostało pomyślnie zmienione.';
            this.passwordForm.reset({
              currentPassword: '',
              newPassword: '',
              confirmPassword: '',
            });
            this.submitted = false;
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
              } else if (errorBody.message) {
                errorMessage = errorBody.message;
              }
            }

            this.saveError = errorMessage;
          },
        });
    } else {
      console.log('Formularz jest niepoprawny.');
    }
  }
}
