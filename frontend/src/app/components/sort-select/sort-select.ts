import { Component, EventEmitter, Output } from '@angular/core';
import { CustomSelect } from '../custom-select/custom-select';
import { SelectOption } from '../../types';

@Component({
  selector: 'app-sort-select',
  standalone: true,
  imports: [CustomSelect],
  templateUrl: './sort-select.html',
})
export class SortSelectComponent {
  @Output() sortChange = new EventEmitter<string | null>();

  sortOptions: SelectOption[] = [
    { label: 'Tytuł A–Z', value: 'TITLE_ASC' },
    { label: 'Tytuł Z–A', value: 'TITLE_DESC' },
    { label: 'Autor A–Z', value: 'AUTHOR_ASC' },
    { label: 'Autor Z–A', value: 'AUTHOR_DESC' },
  ];

  selectedOption: SelectOption | null = null;

  onChange(option: SelectOption | null) {
    this.selectedOption = option;
    this.sortChange.emit(option ? String(option.value) : null);
  }
}
