import { ChannelsPipesModule, ChannelSurchargeType } from '@admin-clients/cpanel/channels/data-access';
import { SeasonTicketChannel, SeasonTicketChannelsService } from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map } from 'rxjs/operators';
import { SeasonTicketChannelSurchargesChannelGenericComponent } from './generic/season-ticket-channel-surcharges-channel-generic.component';
import {
    SeasonTicketChannelSurchargesChannelInvitationComponent
} from './invitation/season-ticket-channel-surcharges-channel-invitation.component';
import {
    SeasonTicketChannelSurchargesChannelPromotionComponent
} from './promotion/season-ticket-channel-surcharges-channel-promotion.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        TranslatePipe,
        ChannelsPipesModule,
        SeasonTicketChannelSurchargesChannelGenericComponent,
        SeasonTicketChannelSurchargesChannelPromotionComponent,
        SeasonTicketChannelSurchargesChannelInvitationComponent
    ],
    selector: 'app-season-ticket-channel-surcharges-channel',
    templateUrl: './season-ticket-channel-surcharges-channel.component.html'
})
export class SeasonTicketChannelSurchargesChannelComponent {
    readonly enabledPromotionRanges$ = inject(SeasonTicketChannelsService).channelSurcharges.get$()
        .pipe(
            filter(Boolean),
            map(surcharges => {
                const promotionSurcharge = surcharges
                    .find(surcharge => surcharge.type === ChannelSurchargeType.promotion);
                return promotionSurcharge?.enabled_ranges;
            })
        );

    @Input() currency: string;
    @Input() channel: SeasonTicketChannel['channel'];
}
