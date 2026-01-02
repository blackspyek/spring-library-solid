import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Observable } from 'rxjs';
import { UserProfile } from '../../types';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-profile-card',
  standalone: true,
  imports: [CommonModule, MatProgressSpinnerModule],
  templateUrl: './profile-card.html',
  styleUrl: './profile-card.scss',
})
export class ProfileCardComponent implements OnInit {
  userProfile$!: Observable<UserProfile>;

  private userService = inject(UserService);

  ngOnInit() {
    this.userProfile$ = this.userService.getCurrentUserProfile();
  }
}
