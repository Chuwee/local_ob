import { AfterViewInit, ChangeDetectionStrategy, Component, inject, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { of } from 'rxjs';
import { first, map, shareReplay, switchMap } from 'rxjs/operators';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { VouchersService } from '@admin-clients/cpanel-vouchers-data-access';
import { CurrencyInputComponent, DialogSize } from '@admin-clients/shared/common/ui/components';

@Component({
    selector: 'app-update-balance-dialog',
    templateUrl: './update-balance-dialog.component.html',
    styleUrls: ['./update-balance-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class UpdateBalanceDialogComponent implements OnInit, AfterViewInit {
    readonly #dialogRef = inject(MatDialogRef<UpdateBalanceDialogComponent>);
    readonly #fb = inject(FormBuilder);
    readonly #voucherSrv = inject(VouchersService);
    readonly #auth = inject(AuthenticationService);

    @ViewChild(CurrencyInputComponent) private readonly _input: CurrencyInputComponent;

    readonly currency$ = this.#voucherSrv.getVoucherGroup$()
        .pipe(
            first(),
            switchMap(voucherGroup => {
                if (voucherGroup.currency_code)  {
                    return of(voucherGroup.currency_code);
                } else {
                    return this.#auth.getLoggedUser$()
                        .pipe(first(), map(user => user.currency));
                }
            }),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly form = this.#fb.group({
        balance: [null as number, [Validators.required, Validators.min(0)]]
    });

    readonly voucher$ = this.#voucherSrv.getVoucher$();

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.SMALL);
        this.#dialogRef.disableClose = false;
    }

    ngAfterViewInit(): void {
        // focus input on start to improve UX
        setTimeout(() => this._input.focus());
    }

    close(): void {
        this.#dialogRef.close();
    }

    submit(): void {
        if (this.form.valid) {
            this.#dialogRef.close(this.form.value.balance);
        }
    }
}
