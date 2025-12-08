import { VoucherOrderDetail } from '@admin-clients/cpanel-sales-data-access';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
    selector: 'app-voucher-order-buyer-data',
    templateUrl: './voucher-order-buyer-data.component.html',
    styleUrls: ['./voucher-order-buyer-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VoucherOrderBuyerDataComponent {

    @Input() voucherOrderDetail: VoucherOrderDetail;

}
