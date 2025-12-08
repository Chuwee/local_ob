import { BiSubscription } from '@admin-clients/cpanel/bi/data-access';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { FlexModule } from '@angular/flex-layout';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        EllipsifyDirective, FlexModule, TranslatePipe, MatMenuModule, MatIcon, MatIconButton
    ],
    selector: 'app-bi-subscriptions-list-cards',
    styleUrls: ['./bi-subscriptions-list-cards.component.scss'],
    templateUrl: './bi-subscriptions-list-cards.component.html'
})
export class BiSubscriptionsListCardsComponent {
    @Input() subscriptions: BiSubscription[];

    @Output() deleteEmitter = new EventEmitter<BiSubscription>();
    @Output() sendSubscriptionEmitter = new EventEmitter<BiSubscription>();
}
