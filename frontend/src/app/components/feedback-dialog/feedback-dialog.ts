import {
  Component,
  inject,
  signal,
  OnInit,
  PLATFORM_ID,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { FeedbackService } from '../../services/feedback.service';
import { FeedbackCategory, FeedbackCategoryOption } from '../../types';

@Component({
  selector: 'app-feedback-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './feedback-dialog.html',
  styleUrl: './feedback-dialog.scss',
})
export class FeedbackDialog implements OnInit {
  private dialogRef = inject(MatDialogRef<FeedbackDialog>);
  private feedbackService = inject(FeedbackService);
  private fb = inject(FormBuilder);
  private platformId = inject(PLATFORM_ID);

  // Form
  feedbackForm!: FormGroup;

  // State signals
  isLoading = signal(false);
  isSuccess = signal(false);
  errorMessage = signal<string | null>(null);
  isRateLimited = signal(false);

  // Category options
  readonly categories: FeedbackCategoryOption[] = [
    { value: 'accessibility', label: 'Dostępność', icon: 'accessibility' },
    { value: 'navigation', label: 'Nawigacja', icon: 'navigation' },
    { value: 'content', label: 'Treść', icon: 'article' },
    { value: 'other', label: 'Inne', icon: 'more_horiz' },
  ];

  readonly maxMessageLength = 2000;

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.feedbackForm = this.fb.group({
      category: ['', Validators.required],
      message: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(this.maxMessageLength)]],
    });
  }

  get messageLength(): number {
    return this.feedbackForm.get('message')?.value?.length || 0;
  }

  get currentPageUrl(): string {
    if (isPlatformBrowser(this.platformId)) {
      return window.location.href;
    }
    return '';
  }

  onSubmit(): void {
    if (this.feedbackForm.invalid || this.isLoading()) {
      this.feedbackForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.isRateLimited.set(false);

    const categoryValue = this.feedbackForm.get('category')?.value as string;
    const request = {
      category: categoryValue.toUpperCase() as FeedbackCategory,
      message: this.feedbackForm.get('message')?.value,
      pageUrl: this.currentPageUrl,
    };

    this.feedbackService.submitFeedback(request).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.isSuccess.set(true);
      },
      error: (err) => {
        this.isLoading.set(false);
        if (err.status === 429) {
          this.isRateLimited.set(true);
          this.errorMessage.set('Przekroczono limit zgłoszeń. Spróbuj ponownie za godzinę.');
        } else {
          this.errorMessage.set(err.message || 'Wystąpił błąd. Spróbuj ponownie.');
        }
      },
    });
  }

  onClose(): void {
    this.dialogRef.close();
  }

  onSuccessClose(): void {
    this.dialogRef.close({ submitted: true });
  }

  hasError(field: string, error: string): boolean {
    const control = this.feedbackForm.get(field);
    return !!(control?.hasError(error) && control?.touched);
  }
}
