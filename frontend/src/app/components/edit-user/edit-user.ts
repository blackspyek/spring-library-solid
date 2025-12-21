import { Component, EventEmitter, Input, Output, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { User, Role } from '../../types';

@Component({
  selector: 'app-edit-user-role',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-user.html',
  styleUrl: './edit-user.scss'
})
export class EditUserRoleComponent implements OnInit {
  private userService = inject(UserService);

  @Input({ required: true }) user!: User;
  @Output() cancel = new EventEmitter<void>();
  @Output() roleUpdated = new EventEmitter<void>();

  availableRoles = [Role.ROLE_READER, Role.ROLE_LIBRARIAN, Role.ROLE_ADMIN];

  selectedRole = signal<Role>(Role.ROLE_READER);
  isProcessing = signal(false);
  errorMessage = signal<string | null>(null);

  ngOnInit() {
    if (this.user.roles && this.user.roles.length > 0) {
      this.selectedRole.set(this.user.roles[0]);
    }
  }

  save() {
    this.isProcessing.set(true);
    this.errorMessage.set(null);

    this.userService.updateUserRole(this.user.email, this.selectedRole()).subscribe({
      next: () => {
        this.isProcessing.set(false);
        this.roleUpdated.emit();
      },
      error: (err: any) => {
        this.isProcessing.set(false);
        this.errorMessage.set(err.error?.message || 'Nie udało się zmienić roli');
      }
    });
  }
}
