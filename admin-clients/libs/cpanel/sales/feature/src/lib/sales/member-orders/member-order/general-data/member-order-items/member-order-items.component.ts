import { MemberOrderDetail } from '@admin-clients/cpanel-sales-data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

@Component({
    selector: 'app-member-order-items',
    templateUrl: './member-order-items.component.html',
    styleUrls: ['./member-order-items.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: [
        trigger('expand', [
            state('void', style({ height: '0px', minHeight: '0', visibility: 'hidden' })),
            state('*', style({ height: '*', visibility: 'visible' })),
            transition('void <=> *', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)'))
        ])
    ],
    standalone: false
})
export class MemberOrderItemsComponent implements OnInit {
    readonly dateTimeFormats = DateTimeFormats;

    @Input() order: MemberOrderDetail;
    @Input() isNewMember: boolean;

    columns: string[];

    constructor() { }

    ngOnInit(): void {
        this.columns = this.isNewMember ? ['name', 'price', 'charges', 'status'] : ['expand', 'allocation', 'name', 'price', 'status'];
    }
}
