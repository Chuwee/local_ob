import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map } from 'rxjs/operators';
import { ChannelSurchargeType } from '@admin-clients/cpanel/channels/data-access';
import { EventChannelsService } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { RangeTableComponent } from '@admin-clients/shared/common/ui/components';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        RangeTableComponent,
        TranslatePipe
    ],
    selector: 'app-event-channel-surcharges-channel-promotion',
    templateUrl: './event-channel-surcharges-channel-promotion.component.html'
})
export class EventChannelSurchargesChannelPromotionComponent {
    readonly data$ = inject(EventChannelsService).getEventChannelChannelSurcharges$()
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
