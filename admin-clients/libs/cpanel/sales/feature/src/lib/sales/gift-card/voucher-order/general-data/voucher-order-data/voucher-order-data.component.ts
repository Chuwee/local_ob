import { VoucherOrderDetail } from '@admin-clients/cpanel-sales-data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
    selector: 'app-voucher-order-data',
    templateUrl: './voucher-order-data.component.html',
    styleUrls: ['./voucher-order-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VoucherOrderDataComponent {
    readonly dateTimeFormats = DateTimeFormats;

    @Input() voucherOrderDetail: VoucherOrderDetail;

}
