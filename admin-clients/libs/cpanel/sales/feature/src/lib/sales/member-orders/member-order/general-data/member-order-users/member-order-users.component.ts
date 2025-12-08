import { MemberOrderDetail, MemberOrderType } from '@admin-clients/cpanel-sales-data-access';
import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
    selector: 'app-member-order-users',
    templateUrl: './member-order-users.component.html',
    styleUrls: ['./member-order-users.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class MemberOrderUsersComponent {
    readonly $order = input.required<MemberOrderDetail>({ alias: 'order' });
    readonly memberOrderType = MemberOrderType;
}
