import { Component, EventEmitter, Input, Output, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { User } from '../../types';

@Component({
  selector: 'app-delete-user',
  standalone: true,
  imports: [CommonModule],
  templateUrl: 'delete-user.html',
  styleUrl: 'delete-user.scss'
})
export class DeleteUserComponent {
  private userService = inject(UserService);

  @Input({ required: true }) user!: User;
  @Output() cancel = new EventEmitter<void>();
  @Output() userDeleted = new EventEmitter<void>();

  isProcessing = signal(false);
  errorMessage = signal<string | null>(null);

  confirm() {
    this.isProcessing.set(true);
    this.errorMessage.set(null);

    this.userService.deleteUser(this.user.id).subscribe({
      next: () => {
        this.isProcessing.set(false);
        this.userDeleted.emit();
      },
      error: (err: any) => {
        this.isProcessing.set(false);

        let message = 'Nie udało się usunąć użytkownika';

        if (err.error?.message && err.error.message !== "An unexpected error occurred") {
          message = err.error.message;
        }
        else if (JSON.stringify(err).includes('foreign key constraint') || JSON.stringify(err).includes('reservation_history')) {
          message = 'Nie można usunąć użytkownika, który posiada historię wypożyczeń lub rezerwacji.';
        }

        this.errorMessage.set(message);
      }
    });
  }
}
