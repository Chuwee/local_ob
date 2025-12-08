import {
    TicketPassbook, ticketPassbookEmptyField,
    TicketPassbookFields, TicketsPassbookService
} from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import { MessageDialogService, ObMatDialogConfig, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Observable, Subject, tap } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
import { TicketPassbookFieldsGroup } from '../models/ticket-passbook-fields-group.enum';
import { ticketPassbookFieldsSettings } from '../models/ticket-passbook-fields-settings';
import { TicketAddContentDialogComponent } from './add-content-dialog/ticket-add-content-dialog.component';

@Component({
    selector: 'app-ticket-passbook-contents-editor',
    templateUrl: './ticket-passbook-contents-editor.component.html',
    styleUrls: ['./ticket-passbook-contents-editor.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class TicketPassbookContentsEditorComponent implements OnInit, OnDestroy {

    private _ticketPassbook: TicketPassbook;
    private _onDestroy = new Subject<void>();
    readonly ticketPassbookFieldsSettings = ticketPassbookFieldsSettings;
    @Input() svgName: string;
    @Input() passbookFields: TicketPassbookFieldsGroup[];
    isInProgress$: Observable<boolean>;
    form: UntypedFormGroup;
    formArrays = new Map<string, UntypedFormArray>();

    constructor(
        private _fb: UntypedFormBuilder,
        private _ticketsPassbookService: TicketsPassbookService,
        private _matDialog: MatDialog,
        private _ephemeralMessageService: EphemeralMessageService,
        private _msgDialogService: MessageDialogService
    ) {
    }

    ngOnInit(): void {
        this.isInProgress$ = booleanOrMerge([
            this._ticketsPassbookService.isTicketPassbookLoading$(),
            this._ticketsPassbookService.isTicketPassbookSaving$()
        ]);
        this.initForm();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._ticketsPassbookService.clearTicketPassbookAvailableFields();
    }

    checkMaxContent(fieldName: TicketPassbookFieldsGroup): boolean {
        const max = ticketPassbookFieldsSettings.get(fieldName).maxContent;
        const values: TicketPassbookFields[] = this.form.get(fieldName).value;
        return max !== undefined && max <= values.filter(field => field.key !== ticketPassbookEmptyField.key).length;
    }

    openAddContentDialog(passbookField: TicketPassbookFieldsGroup, openCustomTab: boolean): void {
        this._matDialog.open(
            TicketAddContentDialogComponent,
            new ObMatDialogConfig({
                ticketPassbook: this._ticketPassbook,
                selectedFields: this.form.get(passbookField).value,
                maxFields: ticketPassbookFieldsSettings.get(passbookField).maxContent,
                passbookKey: passbookField,
                openCustomTab
            })
        )
            .beforeClosed()
            .subscribe(results => this.setNewFields(passbookField, results));
    }

    cancel(): void {
        this.loadData();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<boolean> {
        const ticketPassbookToSave: TicketPassbook = { ...this._ticketPassbook };
        Object.keys(this.form.value).forEach(passbookField => {
            const emptyField = this.form.get(passbookField).value.length === 0;
            if (this.ticketPassbookFieldsSettings.get(passbookField as TicketPassbookFieldsGroup).maxContent === 1) {
                ticketPassbookToSave[passbookField] = emptyField ? ticketPassbookEmptyField : this.form.get(passbookField).value[0];
            } else {
                ticketPassbookToSave[passbookField] = emptyField ? [ticketPassbookEmptyField] : this.form.get(passbookField).value;
            }
        });
        return this._ticketsPassbookService.updateTicketPassbook(ticketPassbookToSave, this._ticketPassbook.entity_id.toString())
            .pipe(
                tap(() => {
                    this._ephemeralMessageService.showSaveSuccess();
                    this.loadData();
                }),
                map(() => true)
            );
    }

    private initForm(): void {
        this.form = this._fb.group({});
        this.passbookFields.forEach(fieldGroup => {
            const formArray = this._fb.array([]);
            this.form.setControl(fieldGroup, formArray);
            this.formArrays.set(fieldGroup, formArray);
        });
        this._ticketsPassbookService.getTicketPassbook$()
            .pipe(takeUntil(this._onDestroy))
            .subscribe(ticketPassbook => this.refreshFormData(ticketPassbook));
    }

    private refreshFormData(ticketPassbook: TicketPassbook): void {
        if (ticketPassbook) {
            this._ticketPassbook = ticketPassbook;
            this.passbookFields.forEach(fieldName => {
                const formArray = this.form.get(fieldName) as UntypedFormArray;
                formArray.clear();
                const fieldValue = ticketPassbook[fieldName];
                (Array.isArray(fieldValue) && fieldValue || [fieldValue])
                    .map(subValue => new UntypedFormControl(subValue))
                    .forEach(control => formArray.push(control));
            });
        }
    }

    private loadData(): void {
        this._ticketsPassbookService.loadTicketPassbook(this._ticketPassbook.code, this._ticketPassbook.entity_id.toString());
        this.form.markAsPristine();
    }

    private setNewFields(passbookField: string, resultFields: TicketPassbookFields[]): void {
        if (resultFields) {
            const formArray = this.form.get(passbookField) as UntypedFormArray;
            const resultControls: UntypedFormControl[] = [];
            formArray.controls.forEach(control => {
                const field = resultFields.find(field => field.key === control.value.key);
                if (field) {
                    resultControls.push(this._fb.control(field));
                }
            });
            resultFields.forEach(field => {
                if (!resultControls.find(control => control.value.key === field.key)) {
                    resultControls.push(this._fb.control(field));
                }
            });
            formArray.clear();
            resultControls.forEach(control => {
                formArray.push(control);
                control.markAsDirty();
            });
        }
    }
}
