import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IonicModule, NavParams } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import { CheckboxListTypeaheadComponent } from '../checkbox-list-typeahead/checkbox-list-typeahead.component';
import { ModalComponent } from '../modal/modal.component';
import { PickerDataItem } from '../picker/models/pickerData';
import { PickerComponent } from '../picker/picker.component';

@Component({
    selector: 'ob-select',
    imports: [CommonModule, IonicModule, FormsModule, CheckboxListTypeaheadComponent, ModalComponent, PickerComponent],
    providers: [NavParams],
    templateUrl: './select.component.html',
    styleUrls: ['./select.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SelectComponent {
    private readonly _translate = inject(TranslateService);

    @Output() readonly valueChange: EventEmitter<any> = new EventEmitter<any>();
    @Input() readonly filterOptions: PickerDataItem[];
    @Input() readonly label: string;
    @Input() readonly searchInputPlaceHolder?: string = '';
    @Input() readonly isMultiple = false;
    @Input() value: any;

    modalIsOpen = false;

    openModal(): void {
        this.modalIsOpen = true;
    }

    closeModal(): void {
        this.modalIsOpen = false;
    }

    onSave(value: any): void {
        this.isMultiple ? this.value = value : this.value = value[0];
        this.valueChange.emit(this.value);
        this.closeModal();
    }

    getFilterInputText(): string {
        if (!this.value || this.value.length === 0) {
            return this._translate.instant('FILTERS.ALL-FILTERS');
        }

        const text = {
            checkboxlist:
                this.isMultiple && this.value.length > 1
                    ? this._translate.instant('FILTERS.SELECTED-FILTERS', {
                        number: this.value?.length
                    })
                    : this.filterOptions.find(filterOption => (
                        filterOption.value ===
                        this.value[0]
                    ))?.label ?? '',
            picker: this.filterOptions.find(filterOption => filterOption.value === this.value)?.label ?? '',
            default: ''
        };

        return text[this.isMultiple ? 'checkboxlist' : 'picker'];
    }
}
