import { TicketPassbookFields } from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { TicketsPassbookCustomFieldTypes } from './ticket-passbook-custom-field-types.enum';

export class TicketPassbookCustomField implements TicketPassbookFields {
    group = '';
    key = null;
    label: string[] = [];
    value: string[] = [];

    constructor(private _fb: UntypedFormBuilder, public field?: TicketPassbookFields) {
        if (field) {
            const { key, label, value } = field;
            this.group = field?.group;
            this.key = key;
            this.label = label;
            this.value = value;
        }
    }

    getFieldAsFormGroup(): UntypedFormGroup {
        const formGroup = this._fb.group({
            title: this._fb.group({
                type: [this.getFieldType(this.label[0]), Validators.required],
                value: [this.label[0], Validators.required]
            }),
            description: this._fb.group({
                type: [this.getFieldType(this.value[0]), Validators.required],
                value: [this.value[0], Validators.required]
            }),
            key: this.key
        });
        return formGroup;
    }

    setFieldValuesFromFormGroup(formValues: Record<string, Record<string, string>>): void {
        this.group = 'custom';
        this.label = [formValues?.['title']?.['value']];
        this.value = [formValues?.['description']?.['value']];
        this.key = formValues?.['key'] ?? `${this.label[0]}_${new Date().getTime()}`;
    }

    toJson(): TicketPassbookFields {
        return {
            group: this.group,
            key: this.key,
            label: this.label,
            value: this.value
        };
    }

    private getFieldType(value: string): TicketsPassbookCustomFieldTypes {
        if (value.includes('{') && value.includes('}')) {
            return TicketsPassbookCustomFieldTypes.value;
        }
        if (!value.includes('{') && !value.includes('}')) {
            return TicketsPassbookCustomFieldTypes.label;
        }
        if (value === 'BLANK_SPACE') {
            return TicketsPassbookCustomFieldTypes.empty;
        }
        return undefined;
    }
}

