import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';
import { FilterOption } from '../../../modules/filters/models/filters.model';

@Component({
    selector: 'checkbox-list-typeahead',
    imports: [CommonModule, IonicModule, TranslatePipe],
    templateUrl: './checkbox-list-typeahead.component.html',
    styleUrls: ['./checkbox-list-typeahead.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CheckboxListTypeaheadComponent implements OnInit {
    private _workingSelectedValues: string[] = [];

    @Output() readonly selectionCancel = new EventEmitter<void>();
    @Output() readonly selectionChange = new EventEmitter<string[]>();
    @Output() readonly saveEmitter: EventEmitter<string[] | number[]> = new EventEmitter();
    @Input() readonly items: FilterOption[] = [];
    @Input() readonly selectedItems: string[] = [];
    @Input() readonly searchPlaceHolder = '';

    filteredItems: FilterOption[] = [];

    ngOnInit(): void {
        this.filteredItems = [...this.items];
        this._workingSelectedValues = this.selectedItems
            ? [...this.selectedItems]
            : [];
    }

    trackItems(index: number, item: FilterOption): string | number | boolean {
        return item.value;
    }

    cancelChanges(): void {
        this.selectionCancel.emit();
    }

    confirmChanges(): void {
        this.selectionChange.emit(this._workingSelectedValues);
    }

    searchbarInput(ev): void {
        this.filterList(ev.target.value);
    }

    filterList(searchQuery: string | undefined): void {
        if (searchQuery === undefined) {
            this.filteredItems = [...this.items];
        } else {
            const normalizedQuery = searchQuery.toLowerCase();
            this.filteredItems = this.items.filter(item => item.label.toLowerCase().includes(normalizedQuery));
        }
    }

    isChecked(value: string | number): boolean {
        return !!this._workingSelectedValues.find(item => item === value);
    }

    checkboxChange(ev): void {
        const { checked, value } = ev.detail;

        if (checked) {
            this._workingSelectedValues = [...this._workingSelectedValues, value];
        } else {
            this._workingSelectedValues = this._workingSelectedValues.filter(
                item => item !== value
            );
        }
    }

    save(): void {
        this.saveEmitter.emit(this._workingSelectedValues);
    }
}
