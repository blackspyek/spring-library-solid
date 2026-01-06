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
  pesel = signal('');
  firstName = signal('');
  lastName = signal('');
  phone = signal('');
  street = signal('');
  city = signal('');
  postalCode = signal('');
  country = signal('');
  buildingNumber = signal('');
  apartmentNumber = signal('');

  isProcessing = signal(false);
  errorMessage = signal<string | null>(null);
  fieldErrors = signal<{ [key: string]: string[] }>({});

  resultPasswordVisible = signal(false);

  showSuccessStep = signal(false);

  toggleResultPassword() {
    this.resultPasswordVisible.update((v) => !v);
  }

  validatePesel(pesel: string): boolean {
    if (!pesel || pesel.length !== 11) return false;
    if (!/^\d{11}$/.test(pesel)) return false;

    const weights = [1, 3, 7, 9, 1, 3, 7, 9, 1, 3];
    let sum = 0;

    for (let i = 0; i < 10; i++) {
      const digit = parseInt(pesel[i], 10);
      sum += (digit * weights[i]) % 10;
    }

    const checksum = (10 - (sum % 10)) % 10;
    const lastDigit = parseInt(pesel[10], 10);

    return checksum === lastDigit;
  }

  validatePostalCode(code: string): boolean {
    return /^\d{2}-\d{3}$/.test(code);
  }

  validatePhone(phone: string): boolean {
    // Polish phone number: 9 digits, optionally with +48 prefix
    if (!phone) return false;
    const cleaned = phone.replace(/[\s-]/g, '');
    return /^(\+48)?\d{9}$/.test(cleaned);
  }

  validateTextOnly(value: string): boolean {
    if (!value) return false;
    // Regex dopuszcza: a-z, A-Z, polskie znaki, spację i myślnik
    return /^[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ\s-]+$/.test(value);
  }

  submit() {
    this.fieldErrors.set({});
    this.errorMessage.set(null);

    // Basic validations
    const errors: { [key: string]: string[] } = {};

    if (!this.email()) {
      errors['email'] = ['Email jest wymagany'];
    } else if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(this.email())) {
      errors['email'] = ['Nieprawidłowy format email'];
    }

    if (!this.firstName()) {
      errors['firstName'] = ['Imię jest wymagane'];
    } else if (!this.validateTextOnly(this.firstName())) {
      errors['firstName'] = ['Imię może zawierać tylko litery'];
    }

    if (!this.lastName()) {
      errors['lastName'] = ['Nazwisko jest wymagane'];
    } else if (!this.validateTextOnly(this.lastName())) {
      errors['lastName'] = ['Nazwisko może zawierać tylko litery'];
    }

    if (!this.phone()) {
      errors['phone'] = ['Telefon jest wymagany'];
    } else if (!this.validatePhone(this.phone())) {
      errors['phone'] = ['Nieprawidłowy format telefonu (np. 123456789 lub +48123456789)'];
    }

    if (!this.pesel()) {
      errors['pesel'] = ['PESEL jest wymagany'];
    } else if (!this.validatePesel(this.pesel())) {
      errors['pesel'] = ['PESEL musi być 11 cyfr i spełniać walidację sumy kontrolnej'];
    }

    if (!this.street()) {
      errors['street'] = ['Ulica jest wymagana'];
    } else if (!this.validateTextOnly(this.street())) {
      errors['street'] = ['Nazwa ulicy może zawierać tylko litery'];
    }

    if (!this.city()) {
      errors['city'] = ['Miasto jest wymagane'];
    } else if (!this.validateTextOnly(this.city())) {
      errors['city'] = ['Nazwa miasta może zawierać tylko litery'];
    }

    if (!this.postalCode()) {
      errors['postalCode'] = ['Kod pocztowy jest wymagany'];
    } else if (!this.validatePostalCode(this.postalCode())) {
      errors['postalCode'] = ['Kod pocztowy musi być w formacie XX-XXX'];
    }

    if (!this.country()) {
      errors['country'] = ['Kraj jest wymagany'];
    } else if (!this.validateTextOnly(this.country())) {
      errors['country'] = ['Nazwa kraju może zawierać tylko litery'];
    }

    if (!this.buildingNumber()) {
      errors['buildingNumber'] = ['Numer budynku jest wymagany'];
    }

    if (Object.keys(errors).length > 0) {
      this.fieldErrors.set(errors);
      this.errorMessage.set('Proszę poprawić błędy w formularzu');
      return;
    }

    this.isProcessing.set(true);

    const request: CreateUserRequest = {
      email: this.email(),
      firstName: this.firstName(),
      lastName: this.lastName(),
      pesel: this.pesel(),
      phone: this.phone(),
      address: {
        street: this.street(),
        city: this.city(),
        postalCode: this.postalCode(),
        country: this.country(),
        buildingNumber: this.buildingNumber(),
        apartmentNumber: this.apartmentNumber() || undefined,
      },
    };

    console.log('Creating user with request:', request);

    this.userService.createUser(request).subscribe({
      next: () => {
        this.isProcessing.set(false);
        this.showSuccessStep.set(true);
      },
      error: (err: any) => {
        this.isProcessing.set(false);

        // Handle different error response formats
        if (err.status === 409) {
          // Conflict - user already exists (from UserAlreadyExistsException)
          this.errorMessage.set(
            err.error?.message || 'Użytkownik z podanym PESEL lub emailem już istnieje w systemie',
          );
        } else if (err.status === 404) {
          // Not found
          this.errorMessage.set(err.error?.message || 'Nie znaleziono zasobu');
        } else if (err.status === 403) {
          // Forbidden - user disabled
          this.errorMessage.set(err.error?.message || 'Użytkownik jest zablokowany');
        } else if (err.error?.errors) {
          // Handle validation errors from API
          const apiErrors: { [key: string]: string[] } = {};

          if (Array.isArray(err.error.errors)) {
            // If errors is an array, create a generic error message
            this.errorMessage.set(err.error.errors.join(', '));
          } else if (typeof err.error.errors === 'object') {
            // If errors is an object with field names as keys
            for (const [field, messages] of Object.entries(err.error.errors)) {
              apiErrors[field] = Array.isArray(messages) ? messages : [String(messages)];
            }
            this.fieldErrors.set(apiErrors);
            this.errorMessage.set('Błąd walidacji danych');
          }
        } else if (err.error?.message) {
          // Generic error message from backend
          this.errorMessage.set(err.error.message);
        } else {
          this.errorMessage.set('Błąd podczas tworzenia użytkownika. Spróbuj ponownie.');
        }
      },
    });
  }

  finish() {
    this.userCreated.emit();
  }
}
