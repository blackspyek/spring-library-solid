import { Component, OnInit, inject } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

import { ReactiveFormsModule, FormGroup, FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-notification-settings-dialog',
  standalone: true,
  imports: [MatButtonModule, ReactiveFormsModule],
  templateUrl: './notification-settings-dialog.html',
  styleUrl: './notification-settings-dialog.scss',
})
export class NotificationSettingsDialog implements OnInit {
  dialogRef = inject<MatDialogRef<NotificationSettingsDialog>>(MatDialogRef);
  private fb = inject(FormBuilder);

  notificationForm!: FormGroup;
  submitted = false;

  ngOnInit(): void {
    this.notificationForm = this.fb.group({
      reservationReady: [false],
      returnReminder: [false],
      newArrivals: [false],
    });
  }

  onNoClick(): void {
    this.dialogRef.close(null);
  }

  onSaveClick(): void {
    this.submitted = true;
    this.notificationForm.markAllAsTouched();

    if (this.notificationForm.valid) {
      this.dialogRef.close(this.notificationForm.value);
    } else {
      console.log('Formularz jest niepoprawny.');
    }
  }
}
