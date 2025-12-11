import { Component, ElementRef, HostListener, inject, input, model, signal } from '@angular/core';
import { SelectOption } from '../../types';

@Component({
  selector: 'app-custom-select',
  imports: [],
  templateUrl: './custom-select.html',
  styleUrl: './custom-select.scss',
})
export class CustomSelect {
  private elementRef = inject(ElementRef);

  label = input<string>('');
  placeholder = input<string>('Wybierz opcję');
  options = input.required<SelectOption[]>();

  selected = model<SelectOption | null>(null);
  activeValues = input<string[]>([]);

  isOpen = signal(false);
  isVisible = signal(false);

  private searchTerm = '';
  private searchTimeout: ReturnType<typeof setTimeout> | null = null;

  toggle() {
    if (this.isOpen()) {
      this.close();
    } else {
      this.open();
    }
  }

  open() {
    this.isVisible.set(true);
    requestAnimationFrame(() => {
      this.isOpen.set(true);
      if (this.selected()) {
        this.scrollToOption(this.selected()!);
      }
    });
  }

  close() {
    this.isOpen.set(false);
    setTimeout(() => {
      this.isVisible.set(false);
      this.searchTerm = '';
    }, 200);
  }

  isOptionActive(option: SelectOption): boolean {
    const active = this.activeValues();
    if (active && active.length) {
      return active.includes(String(option.value));
    }
    return !!this.selected() && this.selected()!.value === option.value;
  }

  selectOption(option: SelectOption) {
    if (this.selected() && this.selected()?.value === option.value) {
      this.selected.set(null);
    } else {
      this.selected.set(option);
    }
    this.close();
  }

  @HostListener('document:click', ['$event'])
  onClickOutside(event: Event) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      if (this.isOpen()) {
        this.close();
      }
    }
  }

  @HostListener('document:keydown', ['$event'])
  onKeyDown(event: KeyboardEvent) {
    if (!this.isOpen()) return;

    if (event.key === 'Escape') {
      this.close();
      return;
    }

    if (event.key === 'Enter') {
      event.preventDefault();
      return;
    }

    if (event.key.length === 1 && /[a-zA-Z0-9ąćęłńóśźżĄĆĘŁŃÓŚŹŻ]/.test(event.key)) {
      event.preventDefault();
      this.handleKeyboardSearch(event.key.toLowerCase());
    }
  }

  private handleKeyboardSearch(key: string) {
    if (this.searchTimeout) {
      clearTimeout(this.searchTimeout);
    }

    this.searchTerm += key;

    const matchingOption = this.options().find((opt) =>
      opt.label.toLowerCase().startsWith(this.searchTerm)
    );

    if (matchingOption) {
      this.scrollToOption(matchingOption);
    }

    this.searchTimeout = setTimeout(() => {
      this.searchTerm = '';
    }, 1000);
  }

  private scrollToOption(option: SelectOption) {
    const dropdown = this.elementRef.nativeElement.querySelector('.select-options');
    if (!dropdown) return;

    const optionIndex = this.options().findIndex((opt) => opt.value === option.value);

    const optionElement = dropdown.children[optionIndex] as HTMLElement;

    if (optionElement) {
      optionElement.scrollIntoView({ block: 'nearest', behavior: 'smooth' });
    }
  }
}
