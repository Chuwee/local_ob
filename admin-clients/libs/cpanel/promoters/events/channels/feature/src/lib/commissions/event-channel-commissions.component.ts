import { ChannelCommissionType } from '@admin-clients/cpanel/channels/data-access';
import { EventChannelRequestStatus, EventChannelsService } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
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
import { EventChannelCommissionsGenericComponent } from './generic/event-channel-commissions-generic.component';
import { EventChannelCommissionsPromotionComponent } from './promotion/event-channel-commissions-promotion.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent,
        CommonModule,
        EventChannelCommissionsGenericComponent,
        EventChannelCommissionsPromotionComponent,
        EmptyStateComponent,
        TranslatePipe,
        MaterialModule,
        FlexLayoutModule
    ],
    selector: 'app-event-channel-commissions',
    templateUrl: './event-channel-commissions.component.html',
    styleUrls: ['./event-channel-commissions.component.scss']
})
export class EventChannelCommissionsComponent implements OnInit, OnDestroy {
    private readonly _eventChannelsSrv = inject(EventChannelsService);

    private readonly _onDestroy = new Subject<void>();

    readonly inProgress$ = booleanOrMerge([
        this._eventChannelsSrv.isEventChannelCommissionsLoading$(),
        this._eventChannelsSrv.eventChannel.inProgress$()
    ]);

    readonly requestAccepted$ = this._eventChannelsSrv.eventChannel.get$()
        .pipe(map(eventChannel => eventChannel?.status.request === EventChannelRequestStatus.accepted));

    readonly disabledPromotionRanges$ = this._eventChannelsSrv.getEventChannelCommissions$()
        .pipe(
            filter(Boolean),
            map(commissions => {
                const promotionComissions = commissions
                    .find(eventChannelComission => eventChannelComission.type === ChannelCommissionType.promotion);
                return !promotionComissions?.enabled_ranges;
            })
        );

    readonly currency$ = inject(EventsService).event.get$()
        .pipe(map(event => event.currency_code));

    ngOnInit(): void {
        this._eventChannelsSrv.eventChannel.get$()
            .pipe(
                filter(Boolean),
                takeUntil(this._onDestroy))
            .subscribe(eventChannel => {
                this._eventChannelsSrv.clearEventChannelCommissions();
                if (eventChannel.status.request === EventChannelRequestStatus.accepted) {
                    this._eventChannelsSrv.loadEventChannelCommissions(eventChannel.event.id, eventChannel.channel.id);
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._eventChannelsSrv.clearEventChannelCommissions();
    }
}
