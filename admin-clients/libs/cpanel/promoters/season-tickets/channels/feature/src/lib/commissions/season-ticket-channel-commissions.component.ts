import { ChannelCommissionType } from '@admin-clients/cpanel/channels/data-access';
import {
    SeasonTicketChannelsService
} from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { EmptyStateComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { filter, map, takeUntil } from 'rxjs/operators';
import {
    SeasonTicketChannelCommissionsGenericComponent
} from './generic/season-ticket-channel-commissions-generic.component';
import {
    SeasonTicketChannelCommissionsPromotionComponent
} from './promotion/season-ticket-channel-commissions-promotion.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        FormContainerComponent,
        TranslatePipe,
        FlexLayoutModule,
        EmptyStateComponent,
        MaterialModule,
        SeasonTicketChannelCommissionsPromotionComponent,
        SeasonTicketChannelCommissionsGenericComponent
    ],
    selector: 'app-season-ticket-channel-commissions',
    templateUrl: './season-ticket-channel-commissions.component.html',
    styleUrls: ['./season-ticket-channel-commissions.component.scss']
})
export class SeasonTicketChannelCommissionsComponent implements OnInit, OnDestroy {
    private readonly _seasonTicketChannelsService = inject(SeasonTicketChannelsService);

    private readonly _onDestroy = new Subject<void>();

    readonly disabledPromotionRanges$ = this._seasonTicketChannelsService.getSeasonTicketChannelCommissions$()
        .pipe(
            filter(Boolean),
            map(commissions => {
                const promotionComissions = commissions
                    .find(stChannelComission => stChannelComission.type === ChannelCommissionType.promotion);
                return !promotionComissions?.enabled_ranges;
            })
        );

    readonly requestAccepted$ = this._seasonTicketChannelsService.isSeasonTicketChannelRequestAccepted$();

    readonly inProgress$ = booleanOrMerge([
        this._seasonTicketChannelsService.isSeasonTicketChannelCommissionsLoading$(),
        this._seasonTicketChannelsService.isSeasonTicketChannelInProgress$()
    ]);

    readonly currency$ = inject(SeasonTicketsService).seasonTicket.get$()
        .pipe(map(event => event.currency_code));

    ngOnInit(): void {
        this._seasonTicketChannelsService.getSeasonTicketChannel$()
            .pipe(
                filter(Boolean),
                takeUntil(this._onDestroy))
            .subscribe(seasonTicketChannel => {
                this._seasonTicketChannelsService.clearSeasonTicketChannelCommissions();
                if (seasonTicketChannel.status.request === 'ACCEPTED') {
                    this._seasonTicketChannelsService.loadSeasonTicketChannelCommissions(
                        seasonTicketChannel.season_ticket.id, seasonTicketChannel.channel.id
                    );
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._seasonTicketChannelsService.clearSeasonTicketChannelCommissions();
    }
}
