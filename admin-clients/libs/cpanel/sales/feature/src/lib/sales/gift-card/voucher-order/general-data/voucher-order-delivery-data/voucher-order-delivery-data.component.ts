import { VoucherOrderDetailItem } from '@admin-clients/cpanel-sales-data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
    selector: 'app-voucher-order-delivery-data',
    templateUrl: './voucher-order-delivery-data.component.html',
    styleUrls: ['./voucher-order-delivery-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VoucherOrderDeliveryDataComponent {
    readonly dateTimeFormats = DateTimeFormats;

    @Input() item: VoucherOrderDetailItem;

}
