import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChangeDetectionStrategy, Component, ElementRef, inject, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { MatDialogRef } from '@angular/material/dialog';
import moment from 'moment';
import { of } from 'rxjs';
import { first, map, shareReplay, switchMap, tap } from 'rxjs/operators';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    PostVoucher, VoucherGroupValidationMethod,
    VoucherLimitlessValue, VouchersService
} from '@admin-clients/cpanel-vouchers-data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { greaterThanValidator, nonZeroValidator } from '@admin-clients/shared/utility/utils';

@Component({
    selector: 'app-new-voucher-groups-dialog',
    templateUrl: './new-voucher-dialog.component.html',
    styleUrls: ['./new-voucher-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class NewVoucherDialogComponent implements OnInit {
    readonly #dialogRef = inject(MatDialogRef<NewVoucherDialogComponent>);
    readonly #fb = inject(FormBuilder);
    readonly #elemRef = inject(ElementRef);
    readonly #voucherSrv = inject(VouchersService);
    readonly #auth = inject(AuthenticationService);
    readonly FORMATS = inject(MAT_DATE_FORMATS);

    readonly isSaving$ = this.#voucherSrv.isVoucherGroupSaving$();
    readonly currency$ = this.#voucherSrv.getVoucherGroup$()
        .pipe(
            first(),
            switchMap(voucherGroup => {
                if (voucherGroup.currency_code) {
                    return of(voucherGroup.currency_code);
                } else {
                    return this.#auth.getLoggedUser$()
                        .pipe(first(), map(user => user.currency));
                }
            }),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    enableLimit = false;
    readonly form = this.#fb.group({
        quantity: [1, greaterThanValidator(0)],
        balance: [null as number, [Validators.required, nonZeroValidator]],
        pin: [{ value: null as string, disabled: true }, [Validators.required]],
        email: null as string,
        expirationEnabled: null as boolean,
        expirationDate: null as string,
        enableLimit: null as boolean,
        limit: [{ value: null as number, disabled: true }, [Validators.required, Validators.min(1)]]
    });

    readonly hasCodeAndPin$ = this.#voucherSrv.getVoucherGroup$()
        .pipe(
            first(),
            map(vg => VoucherGroupValidationMethod.codeAndPin === vg.validation_method),
            tap(hasCodeAndPin => {
                if (hasCodeAndPin) {
                    this.form.controls.pin.enable();
                }
            })
        );

    readonly dateFormat = moment.localeData().longDateFormat(this.FORMATS.display.dateInput).toLowerCase();

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
    }

    toggleLimit(isFixed: boolean): void {
        this.enableLimit = isFixed;
        if (this.enableLimit) {
            this.form.controls.limit.enable({ emitEvent: false });
            this.form.controls.limit.markAsUntouched();
        } else {
            this.form.controls.limit.disable({ emitEvent: false });
            this.form.controls.limit.markAsUntouched();
        }
        this.form.markAsDirty();
    }

    createVoucher(): void {
        this.#voucherSrv.getVoucherGroup$()
            .pipe(first())
            .subscribe(voucherGroup => {
                if (this.form.valid) {
                    const data = this.form.value;
                    const voucher: PostVoucher = {
                        balance: data.balance,
                        expiration: data.expirationDate,
                        pin: data.pin,
                        email: data.email
                    };
                    if (this.enableLimit) {
                        voucher.usage_limit = {
                            type: VoucherLimitlessValue.fixed,
                            value: this.form.value.limit
                        };
                    }
                    if (data.quantity > 1) {
                        const vouchers: PostVoucher[] = [];
                        for (let i = 0; i < data.quantity; i++) {
                            vouchers.push(voucher);
                        }
                        this.#voucherSrv.createVouchers(voucherGroup.id, vouchers)
                            .subscribe(codes => this.close(codes));
                    } else {
                        this.#voucherSrv.createVoucher(voucherGroup.id, voucher)
                            .subscribe(code => this.close([code]));
                    }

                } else {
                    this.form.markAllAsTouched();
                    scrollIntoFirstInvalidFieldOrErrorMsg(this.#elemRef.nativeElement);
                }
            });
    }

    close(codes: string[] = []): void {
        this.#dialogRef.close(codes);
    }
}
