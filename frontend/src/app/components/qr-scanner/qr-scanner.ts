import {
  Component,
  EventEmitter,
  Output,
  signal,
  OnDestroy,
  AfterViewInit,
  ElementRef,
  ViewChild,
  inject,
  PLATFORM_ID,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-qr-scanner',
  standalone: true,
  imports: [CommonModule, MatIcon, FormsModule],
  templateUrl: './qr-scanner.html',
  styleUrl: './qr-scanner.scss',
})
export class QrScannerComponent implements AfterViewInit, OnDestroy {
  @Output() scanned = new EventEmitter<string>();
  @Output() closed = new EventEmitter<void>();

  @ViewChild('videoElement') videoElement!: ElementRef<HTMLVideoElement>;
  @ViewChild('canvasElement') canvasElement!: ElementRef<HTMLCanvasElement>;

  private platformId = inject(PLATFORM_ID);
  private stream: MediaStream | null = null;
  private animationFrameId: number | null = null;

  isLoading = signal(true);
  error = signal<string | null>(null);
  hasCamera = signal(true);

  // Manual input fallback
  manualInput = '';
  showManualInput = signal(false);

  ngAfterViewInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.startCamera();
    }
  }

  ngOnDestroy() {
    this.stopCamera();
  }

  async startCamera() {
    try {
      this.isLoading.set(true);
      this.error.set(null);

      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        throw new Error('Camera API not supported');
      }

      this.stream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: 'environment' },
      });

      const video = this.videoElement.nativeElement;
      video.srcObject = this.stream;
      await video.play();

      this.isLoading.set(false);
      this.scanQRCode();
    } catch (err: any) {
      console.error('Camera error:', err);
      this.isLoading.set(false);
      this.hasCamera.set(false);
      this.error.set('Nie można uzyskać dostępu do kamery. Użyj ręcznego wprowadzania.');
      this.showManualInput.set(true);
    }
  }

  stopCamera() {
    if (this.animationFrameId) {
      cancelAnimationFrame(this.animationFrameId);
      this.animationFrameId = null;
    }

    if (this.stream) {
      this.stream.getTracks().forEach((track) => track.stop());
      this.stream = null;
    }
  }

  scanQRCode() {
    if (!this.stream) return;

    const video = this.videoElement.nativeElement;
    const canvas = this.canvasElement.nativeElement;
    const ctx = canvas.getContext('2d');

    if (!ctx) return;

    const scan = () => {
      if (video.readyState === video.HAVE_ENOUGH_DATA) {
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;
        ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

        const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);

        // Use BarcodeDetector if available (modern browsers)
        if ('BarcodeDetector' in window) {
          const barcodeDetector = new (window as any).BarcodeDetector({
            formats: ['qr_code'],
          });

          barcodeDetector
            .detect(imageData)
            .then((barcodes: any[]) => {
              if (barcodes.length > 0) {
                const code = barcodes[0].rawValue;
                this.handleScannedCode(code);
                return;
              }
            })
            .catch((err: any) => console.error('Barcode detection error:', err));
        }
      }

      this.animationFrameId = requestAnimationFrame(scan);
    };

    scan();
  }

  handleScannedCode(code: string) {
    this.stopCamera();
    this.scanned.emit(code);
  }

  submitManualCode() {
    if (this.manualInput.trim()) {
      this.scanned.emit(this.manualInput.trim());
    }
  }

  close() {
    this.stopCamera();
    this.closed.emit();
  }

  toggleManualInput() {
    this.showManualInput.set(!this.showManualInput());
  }
}
