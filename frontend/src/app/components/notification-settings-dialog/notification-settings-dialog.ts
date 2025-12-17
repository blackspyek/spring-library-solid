import { Component, OnInit, inject } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CommonModule } from '@angular/common';

import { ReactiveFormsModule, FormGroup, FormBuilder } from '@angular/forms';
import { UserService } from '../../services/user-service';
import { NotificationSettings } from '../../types';

@Component({
  selector: 'app-notification-settings-dialog',
  standalone: true,
  imports: [MatButtonModule, ReactiveFormsModule, MatProgressSpinnerModule, CommonModule],
  templateUrl: './notification-settings-dialog.html',
  styleUrl: './notification-settings-dialog.scss',
})
export class NotificationSettingsDialog implements OnInit {
  dialogRef = inject<MatDialogRef<NotificationSettingsDialog>>(MatDialogRef);
  private fb = inject(FormBuilder);
  private userService = inject(UserService);

  notificationForm!: FormGroup;
  submitted = false;
  loading = true;
  saving = false;

  ngOnInit(): void {
    this.notificationForm = this.fb.group({
      reservationReady: [false],
      returnReminder: [false],
      newArrivals: [false],
    });

    this.loadSettings();
  }

  private loadSettings(): void {
    this.userService.getNotificationSettings().subscribe({
      next: (settings: NotificationSettings) => {
        this.notificationForm.patchValue({
          reservationReady: settings.reservationReady,
          returnReminder: settings.returnReminder,
          newArrivals: settings.newArrivals,
        });
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  onNoClick(): void {
    this.dialogRef.close(null);
  }

  onSaveClick(): void {
    this.submitted = true;
    this.notificationForm.markAllAsTouched();

    if (this.notificationForm.valid) {
      this.saving = true;
      const settings: NotificationSettings = this.notificationForm.value;

      this.userService.updateNotificationSettings(settings).subscribe({
        next: (savedSettings) => {
          this.saving = false;
          this.dialogRef.close(savedSettings);
        },
        error: () => {
          this.saving = false;
          console.log('Błąd przy zapisie ustawień powiadomień.');
        },
      });
    } else {
      console.log('Formularz jest niepoprawny.');
    }
  }
}
