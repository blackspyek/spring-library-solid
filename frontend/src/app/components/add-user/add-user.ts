import { Component, EventEmitter, Output, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { UserService, CreateUserRequest } from '../../services/user.service';

@Component({
  selector: 'app-add-user',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIcon],
  templateUrl: './add-user.html',
  styleUrl: './add-user.scss',
})
export class AddUser {
  private userService = inject(UserService);

  @Output() cancel = new EventEmitter<void>();
  @Output() userCreated = new EventEmitter<void>();

  email = signal('');
  password = signal('');

  isProcessing = signal(false);
  errorMessage = signal<string | null>(null);

  tempPasswordVisible = signal(false);
  resultPasswordVisible = signal(false);

  showSuccessStep = signal(false);

  toggleTempPasswordInput() {
    this.tempPasswordVisible.update(v => !v);
  }

  toggleResultPassword() {
    this.resultPasswordVisible.update(v => !v);
  }

  submit() {
    if (!this.email() || !this.password()) {
      this.errorMessage.set('Wypełnij wszystkie pola');
      return;
    }

    if (this.password().length < 8) {
      this.errorMessage.set('Hasło musi mieć co najmniej 8 znaków');
      return;
    }

    this.isProcessing.set(true);
    this.errorMessage.set(null);

    const request: CreateUserRequest = {
      email: this.email(),
      password: this.password()
    };

    this.userService.createUser(request).subscribe({
      next: () => {
        this.isProcessing.set(false);
        this.showSuccessStep.set(true);
      },
      error: (err: any) => {
        this.isProcessing.set(false);
        let msg = err.error?.message || 'Błąd podczas tworzenia użytkownika';        if (err.error?.errors) {
          msg = 'Błąd walidacji danych (np. email już zajęty)';
        }
        this.errorMessage.set(msg);
      }
    });
  }

  finish() {
    this.userCreated.emit();
  }
}
