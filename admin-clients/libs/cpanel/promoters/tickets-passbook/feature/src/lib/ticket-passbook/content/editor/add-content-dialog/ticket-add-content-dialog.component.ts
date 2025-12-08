import {
    TicketPassbook, TicketPassbookFields, TicketPassbookAvailableFields, TicketsPassbookService, ticketPassbookEmptyField
} from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnInit, OnDestroy, Inject } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { combineLatest, Observable, BehaviorSubject, Subject } from 'rxjs';
import { map, startWith, filter, takeUntil, tap } from 'rxjs/operators';
import { TicketPassbookCustomField } from '../../models/ticket-passbook-custom-field.model';

@Component({
    selector: 'app-ticket-add-content-dialog',
    templateUrl: './ticket-add-content-dialog.component.html',
    styleUrls: ['./ticket-add-content-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class TicketAddContentDialogComponent implements OnInit, OnDestroy {

    private _filter = new BehaviorSubject<{ key: string; group: string }>({ key: null, group: null });
    private _ticketPassbook: TicketPassbook;
    private _onDestroy$ = new Subject<void>();
    private _isArrayContent: boolean;
    private _stardardSelectedFields: TicketPassbookFields[];
    private _customSelectedFields: TicketPassbookCustomField[];

    isInProgress$: Observable<boolean>;
    newContentFieldsForm: UntypedFormGroup;
    passbookAvailableFields$: Observable<TicketPassbookAvailableFields[]>;
    maxFields: number;
    standardFieldsSelected$: Observable<number>;
    customFieldsSelected$: Observable<number>;
    totalFieldsSelected$: Observable<number>;
    passbookAvailableTypeFieldsOpts$: Observable<Set<string>>;
    passbookKey: string;
    initialTabIndex: number;

    get customContentsForm(): UntypedFormGroup {
        return this.newContentFieldsForm.get(`${this.passbookKey}_custom`) as UntypedFormGroup;
    }

    constructor(
        private _dialogRef: MatDialogRef<TicketAddContentDialogComponent>,
        private _ticketsPassbookService: TicketsPassbookService,
        private _fb: UntypedFormBuilder,
        @Inject(MAT_DIALOG_DATA) data: {
            ticketPassbook: TicketPassbook;
            selectedFields: TicketPassbookFields[];
            maxFields: number;
            passbookKey: string;
            openCustomTab: boolean;
        }
    ) {
        this._dialogRef.disableClose = false;
        this._stardardSelectedFields = data.selectedFields
            .filter(field => field.key !== ticketPassbookEmptyField.key && field?.group !== 'custom');
        this._customSelectedFields = data.selectedFields
            .filter(field => field?.group === 'custom').map(field => new TicketPassbookCustomField(this._fb, field));
        this.maxFields = data.maxFields;
        this.passbookKey = data.passbookKey;
        this._ticketPassbook = data.ticketPassbook;
        this._isArrayContent = Array.isArray(this._ticketPassbook[this.passbookKey]);
        this._dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
        this.initialTabIndex = data.openCustomTab ? 1 : 0;
    }

    ngOnInit(): void {

        this.isInProgress$ = booleanOrMerge([
            this._ticketsPassbookService.isTicketPassbookAvailableFieldsInProgress$(),
            this._ticketsPassbookService.isTicketPassbookSaving$()
        ]);

        this.loadData();
        this.initForm();

        this.passbookAvailableTypeFieldsOpts$ = this._ticketsPassbookService.getTicketPassbookAvailableFields$()
            .pipe(
                filter(fields => !!fields),
                map(fields => new Set(fields
                    .filter(field => field.key !== ticketPassbookEmptyField.key)
                    .map(field => field.group).filter(group => !!group))));

        this.standardFieldsSelected$ = this.newContentFieldsForm.get(this.passbookKey).valueChanges
            .pipe(
                map(value => value.length),
                startWith(this._stardardSelectedFields.length),
                takeUntil(this._onDestroy$)
            );

        this.customFieldsSelected$ = this.newContentFieldsForm.get(`${this.passbookKey}_custom.customContents`).valueChanges
            .pipe(
                map(value => value.length),
                startWith(this._customSelectedFields.length),
                takeUntil(this._onDestroy$)
            );

        this.totalFieldsSelected$ = combineLatest([this.standardFieldsSelected$, this.customFieldsSelected$])
            .pipe(
                map(([standard = 0, custom = 0]) => standard + custom)
            );

        this.passbookAvailableFields$ = combineLatest([
            this._ticketsPassbookService.getTicketPassbookAvailableFields$(),
            this._filter
        ])
            .pipe(
                filter(([fields]) => !!fields),
                map(([fields, filter]) =>
                    this.filterFields(fields, filter)
                ),
                tap(fields => this.updateForm(fields))
            );
    }

    ngOnDestroy(): void {
        this._ticketsPassbookService.clearTicketPassbookAvailableFields();
    }

    save(): void {
        this.close(this.getFieldsToSave());
    }

    close(saved = false): void {
        this._dialogRef.close(saved);
    }

    setNewFilter(key: string, value: string): void {
        this._filter.next({ ...this._filter.getValue(), [key]: value });
    }

    isCustomTabValid(): boolean {
        return this.customContentsForm?.valid;
    }

    private getFieldsToSave(): any {
        const standardFormValues = this.newContentFieldsForm.getRawValue()[this.passbookKey] || [];
        const customFormValues = this.newContentFieldsForm.getRawValue()[`${this.passbookKey}_custom`].customContents || [];

        if (this._isArrayContent) {
            const customContents: TicketPassbookFields[] = customFormValues.map(field => {
                const customContent: TicketPassbookCustomField = new TicketPassbookCustomField(this._fb);
                customContent.setFieldValuesFromFormGroup(field);
                return customContent.toJson();
            }) || [ticketPassbookEmptyField];
            const values = [...standardFormValues, ...customContents];

            return this.preserveOrderContents(values);
        }

        if (standardFormValues?.length) {
            return standardFormValues;
        }

        if (customFormValues?.length) {
            const customContent = new TicketPassbookCustomField(this._fb);
            customContent.setFieldValuesFromFormGroup(customFormValues[0]);

            return [customContent.toJson()];
        }

        return [ticketPassbookEmptyField];
    }

    private updateForm(stardardFields: TicketPassbookAvailableFields[]): void {
        const isStandardFormFilled = !!this.newContentFieldsForm.get(this.passbookKey).value?.length;
        if (!isStandardFormFilled) {
            const selectedStandardFields = stardardFields.filter(f =>
                this._stardardSelectedFields.find(selected => selected.key === f.key)
            );

            this.newContentFieldsForm.get(this.passbookKey).patchValue(selectedStandardFields);
            this.newContentFieldsForm.updateValueAndValidity();
        }

    }

    private initForm(): void {
        this.newContentFieldsForm = this._fb.group({
            [this.passbookKey]: [null],
            [`${this.passbookKey}_custom`]: this._fb.group({
                customContents: this._fb.array(
                    this._customSelectedFields.map(field => field.getFieldAsFormGroup())
                )
            })
        });
    }

    private loadData(): void {
        const type = this._ticketPassbook.type;
        this._ticketsPassbookService.loadTicketPassbookAvailableFields(type);
        this._ticketsPassbookService.loadTicketPassbookCustomTemplatePlaceholders(type);
        this._ticketsPassbookService.loadTicketPassbookCustomTemplateLiterals();
    }

    private filterFields(
        fields: TicketPassbookAvailableFields[],
        filter: { key?: string; group?: string }
    ): TicketPassbookAvailableFields[] {
        let filteredFields = fields.filter(field => field.key !== ticketPassbookEmptyField.key);
        if (filter?.key) {
            filteredFields = filteredFields.filter(field => field.key.includes(filter.key));
        }

        if (filter?.group) {
            filteredFields = filteredFields.filter(field => field.group === filter.group);
        }

        return [...filteredFields];
    }

    private preserveOrderContents(values: TicketPassbookFields[]): TicketPassbookFields[] {
        const prevContentKeys = this._ticketPassbook[this.passbookKey].map((content: TicketPassbookFields) => content.key);
        const newContentsSelected = values.filter(v => !prevContentKeys.includes(v.key));
        const prevContentsSelected = values.filter(v => prevContentKeys.includes(v.key));

        return [...prevContentsSelected, ...newContentsSelected];
    }

}
