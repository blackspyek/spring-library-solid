import { Component } from '@angular/core';
import { MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { QrCodeComponent } from '../qr-code/qr-code';

@Component({
  selector: 'app-qr-dialog',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule, QrCodeComponent],
  templateUrl: './qr-dialog.html',
  styleUrl: './qr-dialog.scss',
})
export class QrDialog {}
