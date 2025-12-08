import { MemberOrdersService, MemberOrderDetail } from '@admin-clients/cpanel-sales-data-access';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-member-order-details',
    templateUrl: './member-order-details.component.html',
    styleUrls: ['./member-order-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class MemberOrderDetailsComponent implements OnInit, OnDestroy {
    order$: Observable<MemberOrderDetail>;

    constructor(private _memberOrdersSrv: MemberOrdersService) { }

    ngOnInit(): void {
        this.order$ = this._memberOrdersSrv.getMemberOrderDetail$();
    }

    ngOnDestroy(): void {
        this._memberOrdersSrv.clearMemberOrderDetail();
    }
}
