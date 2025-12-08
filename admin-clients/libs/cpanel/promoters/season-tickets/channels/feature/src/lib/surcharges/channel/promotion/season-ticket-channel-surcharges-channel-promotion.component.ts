import { ChannelSurchargeType } from '@admin-clients/cpanel/channels/data-access';
import { SeasonTicketChannelsService } from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import { RangeTableComponent } from '@admin-clients/shared/common/ui/components';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        RangeTableComponent,
        TranslatePipe
    ],
    selector: 'app-season-ticket-channel-surcharges-channel-promotion',
    templateUrl: './season-ticket-channel-surcharges-channel-promotion.component.html'
})
export class SeasonTicketChannelSurchargesChannelPromotionComponent {
    readonly data$ = inject(SeasonTicketChannelsService).channelSurcharges.get$()
        .pipe(
            filter(Boolean),
            map(surcharges =>
                surcharges
                    .find(surcharge => surcharge.type === ChannelSurchargeType.promotion)
                    ?.ranges ?? []
            )
        );

    @Input() currency: string;
}
