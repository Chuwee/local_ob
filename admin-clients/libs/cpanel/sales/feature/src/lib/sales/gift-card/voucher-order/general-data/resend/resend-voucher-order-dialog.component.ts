import { ResendVoucherOrderType, VoucherOrdersService } from '@admin-clients/cpanel-sales-data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-resend-voucher-order-dialog',
    templateUrl: './resend-voucher-order-dialog.component.html',
    styleUrls: ['./resend-voucher-order-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ResendVoucherOrderDialogComponent implements OnInit, OnDestroy {

    private _voucherOrderCode: string;
    private _buyerEmail: string;
    private _receiverEmail: string;
    private _onDestroy: Subject<void> = new Subject();
    resendForm: UntypedFormGroup;
    resendTypesList = Object.values(ResendVoucherOrderType);

    constructor(
        private _fb: UntypedFormBuilder,
        private _voucherOrdersService: VoucherOrdersService,
        private _dialogRef: MatDialogRef<ResendVoucherOrderDialogComponent>,
        @Inject(MAT_DIALOG_DATA) data: { code: string; buyerEmail: string; receiverEmail: string }) {
        this._voucherOrderCode = data.code;
        this._buyerEmail = data.buyerEmail;
        this._receiverEmail = data.receiverEmail || data.buyerEmail;
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.resendForm = this._fb.group({
            email: [this._buyerEmail, [Validators.email]],
            type: [ResendVoucherOrderType.receipt, [Validators.required]]
        });

        this.resendForm.get('type').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(type => {
                if (type === ResendVoucherOrderType.giftCard) {
                    this.resendForm.get('email').patchValue(this._receiverEmail);
                } else {
                    this.resendForm.get('email').patchValue(this._buyerEmail);
                }
            });

    }

    resend(): void {
        let types: ResendVoucherOrderType[] = [this.resendForm.get('type').value];
        if (this.resendForm.get('type').value === ResendVoucherOrderType.both) {
            types = [ResendVoucherOrderType.giftCard, ResendVoucherOrderType.receipt];
        }
        const email: string = this.resendForm.get('email').value;
        this._voucherOrdersService.resend(this._voucherOrderCode, types, email)
            .subscribe(() => this.close(true));
    }

    close(isDone = false): void {
        this._dialogRef.close(isDone);
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }
}
