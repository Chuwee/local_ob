import { OrderDetail, OrdersService } from '@admin-clients/cpanel-sales-data-access';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-order-details',
    templateUrl: './order-details.component.html',
    styleUrls: ['./order-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class OrderDetailsComponent implements OnInit, OnDestroy {
    orderDetail$: Observable<OrderDetail>;
    isLoadingOrSaving$: Observable<boolean>;

    constructor(private _ordersService: OrdersService) { }

    ngOnInit(): void {
        this.isLoadingOrSaving$ = booleanOrMerge([
            this._ordersService.isOrderDetailLoading$(),
            this._ordersService.isResendOrderLoading$(),
            this._ordersService.isTicketsLinkLoading$(),
            this._ordersService.isOrderReloading$()
        ]);
        this.orderDetail$ = this._ordersService.getOrderDetail$();
    }

    ngOnDestroy(): void {
        this._ordersService.clearOrderDetail();
    }
}
