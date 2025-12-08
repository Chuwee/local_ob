import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { CollectivesService, CollectiveValidationMethod, PostCollectiveCode } from '@admin-clients/cpanel/collectives/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { greaterThanValidator, dateIsSameOrBefore, dateTimeGroupValidator } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, ElementRef, Inject, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DATE_FORMATS, MatDateFormats } from '@angular/material/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import moment from 'moment';
import { Observable, Subject } from 'rxjs';

@Component({
    selector: 'app-new-collective-code-dialog',
    templateUrl: './new-collective-code-dialog.component.html',
    styleUrls: ['./new-collective-code-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class NewCollectiveCodeDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _collectiveId: number;

    readonly dateFormat = moment.localeData().longDateFormat(this.FORMATS.display.dateInput).toLowerCase();

    form: UntypedFormGroup;
    isSaving$: Observable<boolean>;
    isUser: boolean;
    isUserPass: boolean;

    constructor(
        private _dialogRef: MatDialogRef<NewCollectiveCodeDialogComponent>,
        private _fb: UntypedFormBuilder,
        private _elemRef: ElementRef,
        private _collectiveSrv: CollectivesService,
        @Inject(MAT_DATE_FORMATS) private readonly FORMATS: MatDateFormats,
        @Inject(MAT_DIALOG_DATA) private _data: { collectiveId: number; validationMethod: CollectiveValidationMethod }
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
        this._collectiveId = _data.collectiveId;
        this.isUser = _data.validationMethod === CollectiveValidationMethod.user;
        this.isUserPass = _data.validationMethod === CollectiveValidationMethod.userPassword;
    }

    ngOnInit(): void {
        this.isSaving$ = this._collectiveSrv.isCollectiveCodeSaving$();

        this.form = this._fb.group({
            code: [{ value: null, disabled: this.isUserPass || this.isUser },
            [Validators.required, Validators.pattern('^[A-Z0-9]+$')]],
            user: [{ value: null, disabled: !(this.isUserPass || this.isUser) },
            [Validators.required, Validators.pattern('^[A-Za-z0-9@._-]+$')]],
            key: [{ value: null, disabled: !this.isUserPass },
            [Validators.required, Validators.pattern('^[a-zA-Z0-9]+$')]],
            usage_limit: [0, [greaterThanValidator(-1)]],
            validity_period: this._fb.group({ from: null, to: null },
                { validators: [dateTimeGroupValidator(dateIsSameOrBefore, 'startDateAfterEndDate', 'from', 'to')] }
            )
        });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(code: string = null): void {
        this._dialogRef.close(code);
    }

    createCollectiveCode(): void {
        this.form.markAllAsTouched();
        if (this.form.valid) {
            const collectiveCodeData: PostCollectiveCode = {
                code: this.form.value.code,
                key: this.form.value.key,
                usage_limit: this.form.value.usage_limit,
                validity_period: this.form.value.validity_period
            };
            if (this.isUserPass || this.isUser) {
                collectiveCodeData.code = this.form.value.user;
            }
            this._collectiveSrv.createCollectiveCode(this._collectiveId, collectiveCodeData)
                .subscribe(() => this.close(collectiveCodeData.code));
        } else {
            scrollIntoFirstInvalidFieldOrErrorMsg(this._elemRef.nativeElement);
        }
    }

}
