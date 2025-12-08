import { MemberOrdersService, MemberOrderDetail, MemberOrderItem } from '@admin-clients/cpanel-sales-data-access';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { Observable, Subject, tap } from 'rxjs';
import { filter, map } from 'rxjs/operators';

@Component({
    selector: 'app-member-order-general-data',
    templateUrl: './member-order-general-data.component.html',
    styleUrls: ['./member-order-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class MemberOrderGeneralDataComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _memberOrdersSrv = inject(MemberOrdersService);

    order$: Observable<MemberOrderDetail>;
    reqInProgress$: Observable<boolean>;
    hasMembership: MemberOrderItem;
    hasAllocation: MemberOrderItem;

    ngOnInit(): void {
        this.reqInProgress$ = booleanOrMerge([
            this._memberOrdersSrv.isMemberOrderDetailLoading$()
        ]);

        this.order$ = this._memberOrdersSrv.getMemberOrderDetail$().pipe(
            filter(order => !!order),
            tap(order => {
                this.hasMembership = order.items.find(item => item.membership);
                this.hasAllocation = order.items.find(item => item.allocation);
            }),
            // sort items to have always the manager user first
            map(order => ({
                ...order,
                items: order.items.sort(a => a.member?.partner_id === order.buyer_data?.partner_id ? -1 : +a.member?.id_number || 0)
            }))
        );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }
}
