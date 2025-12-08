import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { MemberOrderType, MemberOrderDetail } from '@admin-clients/cpanel-sales-data-access';
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { first, map, Observable } from 'rxjs';

@Component({
    selector: 'app-member-order-price',
    templateUrl: './member-order-price.component.html',
    styleUrls: ['./member-order-price.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class MemberOrderPriceComponent implements OnInit {
    readonly orderTypes = MemberOrderType;
    currency$: Observable<string>;

    @Input() order: MemberOrderDetail;
    constructor(
        private _auth: AuthenticationService
    ) { }

    ngOnInit(): void {
        this.currency$ = this._auth.getLoggedUser$().pipe(first(user => !!user), map(user => user.currency));
    }
}
