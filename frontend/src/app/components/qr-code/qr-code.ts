import {
  Component,
  inject,
  OnInit,
  OnDestroy,
  ViewChild,
  ElementRef,
  Renderer2,
  ChangeDetectorRef,
} from '@angular/core';

import { UserProfile } from '../../types';
import { Observable, Subscription } from 'rxjs';
import { UserService } from '../../services/user-service';
import QRCode from 'qrcode';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-qr-code',
  templateUrl: './qr-code.html',
  styleUrl: './qr-code.scss',
  standalone: true,
  imports: [MatProgressSpinnerModule],
})
export class QrCodeComponent implements OnInit, OnDestroy {
  private userService = inject(UserService);
  private renderer = inject(Renderer2);
  private cdr = inject(ChangeDetectorRef);

  public userProfile$!: Observable<UserProfile>;
  private subscription!: Subscription;

  @ViewChild('qrCodeContainer')
  qrCodeContainer!: ElementRef<HTMLCanvasElement>;

  public profile: UserProfile | null = null;

  ngOnInit() {
    this.userProfile$ = this.userService.getCurrentUserProfile();

    this.subscription = this.userProfile$.subscribe((profile) => {
      this.profile = profile;
      if (profile?.readerId) {
        this.cdr.detectChanges();

        this.generateQrCode(profile.readerId);
      }
    });
  }
  private generateQrCode(readerId: string): void {
    if (!this.qrCodeContainer) {
      console.error('Brak kontenera QR Code w cyklu ViewChild.');
      return;
    }
    const element = this.qrCodeContainer.nativeElement;
    while (element.firstChild) {
      this.renderer.removeChild(element, element.firstChild);
    }

    QRCode.toCanvas(
      element,
      readerId,
      {
        width: 135,
        margin: 0,
        errorCorrectionLevel: 'H',
        color: {
          dark: '#000000',
          light: '#0000',
        },
      },
      (error: Error | null | undefined) => {
        if (error) console.error('Błąd generowania QR Code:', error);
      }
    );
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
