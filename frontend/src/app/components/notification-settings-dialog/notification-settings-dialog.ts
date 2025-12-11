import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';

import { ReactiveFormsModule, FormGroup, FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-notification-settings-dialog',
  standalone: true,
  imports: [CommonModule, MatButtonModule, ReactiveFormsModule],
  templateUrl: './notification-settings-dialog.html',
  styleUrl: './notification-settings-dialog.scss',
})
export class NotificationSettingsDialog implements OnInit {
  notificationForm!: FormGroup;
  submitted = false;

  constructor(
    public dialogRef: MatDialogRef<NotificationSettingsDialog>,
    private fb: FormBuilder,
  ) {}

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
