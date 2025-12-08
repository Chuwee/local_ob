import { OrderDetail } from '@admin-clients/cpanel-sales-data-access';
import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-order-details-b2b-buyer-data',
    templateUrl: './b2b-buyer-data.component.html',
    styleUrls: ['./b2b-buyer-data.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [TranslatePipe, NgIf]
})
export class OrderDetailsB2BBuyerDataComponent {
    @Input() order: OrderDetail;
}
