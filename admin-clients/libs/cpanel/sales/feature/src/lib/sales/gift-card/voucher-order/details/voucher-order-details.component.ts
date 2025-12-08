import { VoucherOrdersService, VoucherOrderDetail } from '@admin-clients/cpanel-sales-data-access';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-voucher-order-details',
    templateUrl: './voucher-order-details.component.html',
    styleUrls: ['./voucher-order-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VoucherOrderDetailsComponent implements OnInit, OnDestroy {
    orderDetail$: Observable<VoucherOrderDetail>;

    constructor(private _voucherOrdersSrv: VoucherOrdersService) { }

    ngOnInit(): void {
        this.orderDetail$ = this._voucherOrdersSrv.getVoucherOrderDetail$();
    }

    ngOnDestroy(): void {
        this._voucherOrdersSrv.clearVoucherOrderDetail();
    }
}
