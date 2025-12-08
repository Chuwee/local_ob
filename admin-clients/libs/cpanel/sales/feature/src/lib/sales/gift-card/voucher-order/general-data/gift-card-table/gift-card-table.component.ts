import { VoucherOrderDetail, VoucherOrderDetailItem } from '@admin-clients/cpanel-sales-data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Router } from '@angular/router';

@Component({
    selector: 'app-order-details-gift-card-table',
    templateUrl: './gift-card-table.component.html',
    styleUrls: ['./gift-card-table.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VoucherOrderDetailsGiftCardTableComponent {

    @Input() items: VoucherOrderDetail['items'];

    readonly dateTimeFormats = DateTimeFormats;

    cardColumns = ['card', 'code', 'expiration', 'price', 'status'];

    constructor(private _router: Router) { }

    goToVoucherGroup(item: VoucherOrderDetailItem): void {
        if (item.group) {
            this._router.navigate(['vouchers', item.group.id, 'voucher-codes', item.code]);
        }
    }
}
