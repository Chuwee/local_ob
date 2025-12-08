import { VoucherOrdersService, VoucherOrderDetail } from '@admin-clients/cpanel-sales-data-access';
import { EphemeralMessageService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable, Subject } from 'rxjs';
import { filter } from 'rxjs/operators';
import { ResendVoucherOrderDialogComponent } from './resend/resend-voucher-order-dialog.component';

@Component({
    selector: 'app-voucher-order-general-data',
    templateUrl: './voucher-order-general-data.component.html',
    styleUrls: ['./voucher-order-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VoucherOrderGeneralDataComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();

    voucherOrderDetail$: Observable<VoucherOrderDetail>;
    reqInProgress$: Observable<boolean>;

    constructor(
        private _voucherOrdersSrv: VoucherOrdersService,
        private _ephemeralSrv: EphemeralMessageService,
        private _matDialog: MatDialog
    ) { }

    ngOnInit(): void {
        this.reqInProgress$ = booleanOrMerge([
            this._voucherOrdersSrv.isVoucherOrderDetailLoading$(),
            this._voucherOrdersSrv.isResendLoading$()
        ]);

        this.voucherOrderDetail$ = this._voucherOrdersSrv.getVoucherOrderDetail$().pipe(
            filter(orderDetail => !!orderDetail)
        );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    openResendDialog(code: string, buyerEmail: string, receiverEmail: string = null): void {
        this._matDialog.open(ResendVoucherOrderDialogComponent,
            new ObMatDialogConfig({ code, buyerEmail, receiverEmail }))
            .beforeClosed()
            .pipe(filter(done => done))
            .subscribe(() => {
                this._ephemeralSrv.showSuccess({ msgKey: 'FORMS.FEEDBACK.ACTION_SUCCESS' });
            });
    }
}
