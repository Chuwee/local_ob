import { BiSubscription } from '@admin-clients/cpanel/bi/data-access';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { FlexModule } from '@angular/flex-layout';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexModule, TranslatePipe, MatTableModule, MatIcon, MatTooltip, MatIconButton
    ],
    selector: 'app-bi-subscriptions-list-table',
    templateUrl: './bi-subscriptions-list-table.component.html'
})
export class BiSubscriptionsListTableComponent {
    readonly columns = ['report', 'name', 'scheduled', 'actions'];

    @Input() subscriptions: BiSubscription[];

    @Output() deleteEmitter = new EventEmitter<BiSubscription>();
    @Output() sendSubscriptionEmitter = new EventEmitter<BiSubscription>();
}
