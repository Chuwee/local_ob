import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { map, Observable, Subject, takeUntil } from 'rxjs';
import { ResendVoucherRequest, VoucherEmailFormat, VoucherGroupType, VouchersService } from '@admin-clients/cpanel-vouchers-data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';

@Component({
    selector: 'app-voucher-email-dialog',
    templateUrl: './voucher-email-dialog.component.html',
    styleUrls: ['./voucher-email-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VoucherEmailDialogComponent implements OnInit, OnDestroy {

    private _voucherEmail: string;
    private _onDestroy = new Subject<void>();
    private _voucherGroupId: number;
    private _code: string;

    languages: string[];
    type: VoucherGroupType;
    form: UntypedFormGroup;
    basicForm: UntypedFormGroup;
    voucherGroupType = VoucherGroupType;
    voucherEmailFormat = VoucherEmailFormat;
    enableNonRefundFields$: Observable<boolean>;

    constructor(
        private _fb: UntypedFormBuilder,
        private _vouchersSrv: VouchersService,
        private _dialogRef: MatDialogRef<VoucherEmailDialogComponent>,
        @Inject(MAT_DIALOG_DATA) data: {
            type: VoucherGroupType;
            voucherEmail: string;
            languages: string[];
            voucherGroupId: number;
            code: string;
        }
    ) {
        this._voucherEmail = data.voucherEmail;
        this.languages = data.languages;
        this.type = data.type;
        this._voucherGroupId = data.voucherGroupId;
        this._code = data.code;
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.basicForm = this._fb.group({
            language: null,
            subject: null,
            body: null
        });

        this.form = this._fb.group({
            format: [null, Validators.required],
            email: [this._voucherEmail, [Validators.required, Validators.email]],
            basicForm: this.basicForm
        });

        if (this.type === this.voucherGroupType.manual) {
            this.form.get('format').clearValidators();
            Object.keys(this.basicForm.controls).forEach(controlKey =>
                this.basicForm.controls[controlKey].setValidators(Validators.required));
        }

        this.enableNonRefundFields$ = this.form.get('format').valueChanges
            .pipe(
                takeUntil(this._onDestroy),
                map(format => {
                    const controls = Object.keys(this.basicForm.controls);
                    if (format === this.voucherEmailFormat.basic) {
                        this.form.get('email').setValidators([Validators.required, Validators.email]);
                        controls.forEach(controlKey => this.basicForm.controls[controlKey].setValidators(Validators.required));
                    } else {
                        this.form.get('email').setValidators(Validators.email);
                        controls.forEach(controlKey => this.basicForm.controls[controlKey].clearValidators());
                    }
                    this.form.get('email').updateValueAndValidity();
                    controls.forEach(controlKey => this.basicForm.controls[controlKey].updateValueAndValidity());
                    return format !== this.voucherEmailFormat.refund;
                })
            );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    resend(): void {
        if (this.form.valid) {
            const { format: type, email } = this.form.value;
            const { language, subject, body } = this.basicForm.value;
            const payload: ResendVoucherRequest = this.type === this.voucherGroupType.refund
                ? { language, type, email, subject, body }
                : { type: this.voucherEmailFormat.basic, language, email, subject, body };
            this._vouchersSrv.resend(this._voucherGroupId, this._code, payload).subscribe(() => this.close(true));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        }
    }

    close(isDone = false): void {
        this._dialogRef.close(isDone);
    }
}
