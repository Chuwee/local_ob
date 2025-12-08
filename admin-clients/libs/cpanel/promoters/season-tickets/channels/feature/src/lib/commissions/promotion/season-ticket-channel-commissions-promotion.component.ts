import { ChannelCommissionType } from '@admin-clients/cpanel/channels/data-access';
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
    selector: 'app-season-ticket-channel-commissions-promotion',
    templateUrl: './season-ticket-channel-commissions-promotion.component.html'
})
export class SeasonTicketChannelCommissionsPromotionComponent {
    readonly data$ = inject(SeasonTicketChannelsService).getSeasonTicketChannelCommissions$()
        .pipe(
            filter(Boolean),
            map(commissions =>
                commissions
                    .find(commission => commission.type === ChannelCommissionType.promotion)
                    ?.ranges ?? []
            )
        );

    @Input() currency: string;
}
