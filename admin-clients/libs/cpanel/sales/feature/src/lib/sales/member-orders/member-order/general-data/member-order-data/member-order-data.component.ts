
import { MemberOrderDetail } from '@admin-clients/cpanel-sales-data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
    selector: 'app-member-order-data',
    templateUrl: './member-order-data.component.html',
    styleUrls: ['./member-order-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class MemberOrderDataComponent {
    @Input() order: MemberOrderDetail;

    readonly dateTimeFormats = DateTimeFormats;

    constructor() { }
}
