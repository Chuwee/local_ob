import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map } from 'rxjs/operators';
import { ChannelsPipesModule, ChannelSurchargeType } from '@admin-clients/cpanel/channels/data-access';
import { EventChannel, EventChannelsService } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import {
    EventChannelSurchargesChannelGenericComponent
} from './generic/event-channel-surcharges-channel-generic.component';
import {
    EventChannelSurchargesChannelInvitationComponent
} from './invitation/event-channel-surcharges-channel-invitation.component';
import {
    EventChannelSurchargesChannelPromotionComponent
} from './promotion/event-channel-surcharges-channel-promotion.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        TranslatePipe,
        EventChannelSurchargesChannelGenericComponent,
        ChannelsPipesModule,
        EventChannelSurchargesChannelInvitationComponent,
        EventChannelSurchargesChannelPromotionComponent
    ],
    selector: 'app-event-channel-surcharges-channel',
    templateUrl: './event-channel-surcharges-channel.component.html'
})
export class EventChannelSurchargesChannelComponent {
    readonly enabledPromotionRanges$ = inject(EventChannelsService).getEventChannelChannelSurcharges$()
        .pipe(
            filter(Boolean),
            map(surcharges => {
                const promotionSurcharge = surcharges
                    .find(surcharge => surcharge.type === ChannelSurchargeType.promotion);
                return promotionSurcharge?.enabled_ranges;
            })
        );

    @Input() currency: string;
    @Input() channel: EventChannel['channel'];
}
