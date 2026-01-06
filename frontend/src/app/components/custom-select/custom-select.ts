import { Component, ElementRef, HostListener, inject, input, model, signal, computed } from '@angular/core';
import { SelectOption } from '../../types';

let nextSelectId = 0;

@Component({
  selector: 'app-custom-select',
  imports: [],
  templateUrl: './custom-select.html',
  styleUrl: './custom-select.scss',
})
export class CustomSelect {
  private elementRef = inject(ElementRef);

  // Generowanie unikalnego ID dla każdej instancji
  readonly selectId = `custom-select-${nextSelectId++}`;

  label = input<string>('');
  placeholder = input<string>('Wybierz opcję');
  options = input.required<SelectOption[]>();
  ariaLabel = input<string>('');

  selected = model<SelectOption | null>(null);
  activeValues = input<string[]>([]);

  isOpen = signal(false);
  isVisible = signal(false);

  // Indeks aktywnej opcji dla nawigacji klawiaturą
  activeIndex = signal(-1);

  // Computed dla aria-activedescendant
  activeDescendantId = computed(() => {
    const idx = this.activeIndex();
    return idx >= 0 ? `${this.selectId}-option-${idx}` : null;
  });

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
    // Ustaw aktywny indeks na wybraną opcję lub pierwszą
    const selectedIdx = this.selected()
      ? this.options().findIndex(opt => opt.value === this.selected()!.value)
      : 0;
    this.activeIndex.set(selectedIdx >= 0 ? selectedIdx : 0);

    requestAnimationFrame(() => {
      this.isOpen.set(true);
      if (this.selected()) {
        this.scrollToOption(this.selected()!);
      }
    });
  }

  close() {
    this.isOpen.set(false);
    this.activeIndex.set(-1);
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

  isOptionHighlighted(index: number): boolean {
    return this.activeIndex() === index;
  }

  selectOption(option: SelectOption) {
    if (this.selected() && this.selected()?.value === option.value) {
      this.selected.set(null);
    } else {
      this.selected.set(option);
    }
    this.close();
  }

  selectActiveOption() {
    const idx = this.activeIndex();
    if (idx >= 0 && idx < this.options().length) {
      this.selectOption(this.options()[idx]);
    }
  }

  @HostListener('document:click', ['$event'])
  onClickOutside(event: Event) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      if (this.isOpen()) {
        this.close();
      }
    }
  }

  @HostListener('keydown', ['$event'])
  onKeyDown(event: KeyboardEvent) {
    const opts = this.options();

    switch (event.key) {
      case 'Enter':
      case ' ':
        event.preventDefault();
        if (this.isOpen()) {
          this.selectActiveOption();
        } else {
          this.open();
        }
        break;

      case 'Escape':
        if (this.isOpen()) {
          event.preventDefault();
          event.stopPropagation();
          this.close();
          // Zwróć fokus do przycisku
          const button = this.elementRef.nativeElement.querySelector('.select-button');
          button?.focus();
        }
        break;

      case 'ArrowDown':
        event.preventDefault();
        if (!this.isOpen()) {
          this.open();
        } else {
          const nextIdx = Math.min(this.activeIndex() + 1, opts.length - 1);
          this.activeIndex.set(nextIdx);
          this.scrollToActiveOption();
        }
        break;

      case 'ArrowUp':
        event.preventDefault();
        if (!this.isOpen()) {
          this.open();
        } else {
          const prevIdx = Math.max(this.activeIndex() - 1, 0);
          this.activeIndex.set(prevIdx);
          this.scrollToActiveOption();
        }
        break;

      case 'Home':
        if (this.isOpen()) {
          event.preventDefault();
          this.activeIndex.set(0);
          this.scrollToActiveOption();
        }
        break;

      case 'End':
        if (this.isOpen()) {
          event.preventDefault();
          this.activeIndex.set(opts.length - 1);
          this.scrollToActiveOption();
        }
        break;

      case 'Tab':
        if (this.isOpen()) {
          this.close();
        }
        break;

      default:
        // Wyszukiwanie przez wpisywanie
        if (this.isOpen() && event.key.length === 1 && /[a-zA-Z0-9ąćęłńóśźżĄĆĘŁŃÓŚŹŻ]/.test(event.key)) {
          event.preventDefault();
          this.handleKeyboardSearch(event.key.toLowerCase());
        }
        break;
    }
  }

  private handleKeyboardSearch(key: string) {
    if (this.searchTimeout) {
      clearTimeout(this.searchTimeout);
    }

    this.searchTerm += key;

    const matchingIndex = this.options().findIndex((opt) =>
      opt.label.toLowerCase().startsWith(this.searchTerm)
    );

    if (matchingIndex >= 0) {
      this.activeIndex.set(matchingIndex);
      this.scrollToActiveOption();
    }

    this.searchTimeout = setTimeout(() => {
      this.searchTerm = '';
    }, 1000);
  }

  private scrollToActiveOption() {
    const dropdown = this.elementRef.nativeElement.querySelector('.select-options');
    if (!dropdown) return;

    const idx = this.activeIndex();
    const optionElement = dropdown.children[idx] as HTMLElement;

    if (optionElement) {
      optionElement.scrollIntoView({ block: 'nearest', behavior: 'smooth' });
    }
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
