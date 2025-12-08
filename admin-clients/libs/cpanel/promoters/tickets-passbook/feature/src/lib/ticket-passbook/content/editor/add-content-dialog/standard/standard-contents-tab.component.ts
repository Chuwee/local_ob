import { TicketPassbookAvailableFields } from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { MatListOption } from '@angular/material/list';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-standard-contents-tab',
    templateUrl: './standard-contents-tab.component.html',
    styleUrls: ['./standard-contents-tab.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class TicketStandardContentsTabComponent {

    @Output() filterChange = new EventEmitter<{ key: string; value: string }>();
    @Input() passbookAvailableTypeFieldsOpts$: Observable<Set<string>>;
    @Input() formGroup: UntypedFormGroup;
    @Input() passbookAvailableFields$: Observable<TicketPassbookAvailableFields[]>;
    @Input() passbookKey: string;
    @Input() maxFields: number;
    @Input() totalFieldsSelected: number;

    get standardContentsControl(): UntypedFormControl {
        return this.formGroup.get(this.passbookKey) as UntypedFormControl;
    }

    filterFormGroup: UntypedFormGroup;

    emitFilterChange(value: string): void {
        this.filterChange.emit({ key: 'group', value });
    }

    emitSearchInputChange(value: string): void {
        this.filterChange.emit({ key: 'key', value });
    }

    updateSearchValue(value: string): void {
        this.filterFormGroup.get('searchValue').setValue(value);
    }

    onSelectionChange(option: MatListOption): void {
        let matOptions = this.standardContentsControl.value || [];
        if (option.selected) {
            matOptions.push(option.value);
        } else {
            matOptions = matOptions.filter((opt: TicketPassbookAvailableFields) => opt !== option.value);
        }
        this.standardContentsControl.setValue(matOptions);
    }

}
