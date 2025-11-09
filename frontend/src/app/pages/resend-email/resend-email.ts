import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-resend-email',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './resend-email.html',
  styleUrl: './resend-email.scss',
})
export class ResendEmail {
  private fb = inject(FormBuilder);
  resendEmailForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
  });

  onSubmit() {
    this.resendEmailForm.markAllAsTouched();

    if (this.resendEmailForm.valid) {
      console.log(this.resendEmailForm.valid);
    } else {
      console.log('Form is invalid');
    }
  }

  get email() {
    return this.resendEmailForm.get('email');
  }
}
