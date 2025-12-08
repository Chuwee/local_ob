import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { CollectiveCode, CollectivesService, PutCollectiveCode } from '@admin-clients/cpanel/collectives/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { greaterThanValidator, dateIsSameOrBefore, dateTimeGroupValidator } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, ElementRef, Inject, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { MAT_DATE_FORMATS, MatDateFormats } from '@angular/material/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import moment from 'moment';
import { Observable, Subject } from 'rxjs';

@Component({
    selector: 'app-edit-collective-code-dialog',
    templateUrl: './edit-collective-code-dialog.component.html',
    styleUrls: ['./edit-collective-code-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class EditCollectiveCodeDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _collectiveId: number;
    private _collectiveCodes: CollectiveCode[];
    private _q: string;
    readonly dateFormat = moment.localeData().longDateFormat(this.FORMATS.display.dateInput).toLowerCase();

    form: UntypedFormGroup;
    numCollectiveCodes: number;
    isSaving$: Observable<boolean>;

    constructor(
        private _dialogRef: MatDialogRef<EditCollectiveCodeDialogComponent>,
        private _fb: UntypedFormBuilder,
        private _elemRef: ElementRef,
        private _collectiveSrv: CollectivesService,
        @Inject(MAT_DATE_FORMATS) private readonly FORMATS: MatDateFormats,
        @Inject(MAT_DIALOG_DATA) private _data: {
            collectiveId: number; collectiveCodes: CollectiveCode[];
            codesSelected: number; q: string;
        }
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
        this._collectiveId = _data.collectiveId;
        this._collectiveCodes = _data.collectiveCodes;
        this._q = _data.q;
        this.numCollectiveCodes = _data.codesSelected || 0;
    }

    ngOnInit(): void {
        this.isSaving$ = this._collectiveSrv.isCollectiveCodeSaving$();

        this.form = this._fb.group({
            usage_limit: [{ value: 0, disabled: true }, [greaterThanValidator(-1)]],
            validity_period: this._fb.group({
                from: { value: null, disabled: true },
                to: { value: null, disabled: true }
            }, { validators: [dateTimeGroupValidator(dateIsSameOrBefore, 'startDateAfterEndDate', 'from', 'to')] })
        });

        if (this.numCollectiveCodes === 1) {
            this.form.patchValue({
                usage_limit: this._collectiveCodes[0]?.usage?.limit,
                validity_period: {
                    from: this._collectiveCodes[0]?.validity_period?.from,
                    to: this._collectiveCodes[0]?.validity_period?.to
                }
            });
        }
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    enableOrDisableForm(formEnabled: boolean, formKey: string): void {
        if (formEnabled) {
            this.form.get(formKey).enable();
        } else {
            this.form.get(formKey).disable();
        }
    }

    saveCollectiveCodes(): void {
        if (this.form.valid) {
            const collectiveCodeData: PutCollectiveCode = {
                usage_limit: this.form.value.usage_limit
            };
            const fromForm = this.form.get('validity_period.from') as UntypedFormControl;
            const toForm = this.form.get('validity_period.to') as UntypedFormControl;
            if (fromForm.enabled || toForm.enabled) {
                collectiveCodeData.validity_period = {
                    from: fromForm.enabled ? fromForm.value || null : undefined,
                    to: toForm.enabled ? toForm.value || null : undefined
                };
            }
            this._collectiveSrv.saveCollectiveCode(
                this._collectiveId, this._collectiveCodes.map(collectiveCode => collectiveCode.code), collectiveCodeData, this._q
            ).subscribe(() => this.close(true));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this._elemRef.nativeElement);
        }
    }

    close(edited = false): void {
        this._dialogRef.close(edited);
    }
}
