import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { VoucherOrderDetail } from '@admin-clients/cpanel-sales-data-access';
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { first, map, Observable } from 'rxjs';

@Component({
    selector: 'app-voucher-order-price',
    templateUrl: './voucher-order-price.component.html',
    styleUrls: ['./voucher-order-price.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VoucherOrderPriceComponent implements OnInit {
    currency$: Observable<string>;

    @Input() voucherOrderDetail: VoucherOrderDetail;

    constructor(private _auth: AuthenticationService) { }

    ngOnInit(): void {
        this.currency$ = this._auth.getLoggedUser$().pipe(first(user => !!user), map(user => user.currency));
    }
}
