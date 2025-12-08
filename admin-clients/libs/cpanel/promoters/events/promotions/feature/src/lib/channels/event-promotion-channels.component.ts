import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { PromotionChannelsScope } from '@admin-clients/cpanel/promoters/data-access';
import {
    EventChannelsService, eventChannelsProviders
} from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventPromotionsService } from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import { HelpButtonComponent, SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { IdName, PageableFilter } from '@admin-clients/shared/data-access/models';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy,
    Component,
    Input,
    OnInit,
    inject,
    ChangeDetectorRef,
    DestroyRef
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { filter, map, startWith, shareReplay, take } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        HelpButtonComponent,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        MaterialModule,
        SearchablePaginatedSelectionModule,
        CommonModule
    ],
    selector: 'app-event-promotion-channels',
    templateUrl: './event-promotion-channels.component.html',
    styleUrls: ['./event-promotion-channels.component.scss'],
    providers: [
        eventChannelsProviders
    ]
})
export class EventPromotionChannelsComponent implements OnInit {
    private readonly _changeDetRef = inject(ChangeDetectorRef);
    private readonly _eventsService = inject(EventsService);
    private readonly _eventChannelService = inject(EventChannelsService);
    private readonly _eventPromotionsService = inject(EventPromotionsService);
    private readonly _destroyRef = inject(DestroyRef);

    private readonly _channelsFilters = new BehaviorSubject({ type: null as ChannelType, name: null as string });

    readonly channelType = ChannelType;
    readonly promotionChannelsScope = PromotionChannelsScope;
    readonly metadata$ = this._eventChannelService.eventChannelsList.getMetaData$();
    readonly isLoading$ = this._eventChannelService.eventChannelsList.inProgress$();

    channelsListData$: Observable<IdName[]>;

    @Input() channelsForm: UntypedFormGroup;
    @Input() eventId: number;
    @Input() promotionId: number;
    @Input() isPresale$: Observable<boolean>;
    @Input() fixedTypeFilter$: Observable<ChannelType>;
    @Input() pageSize: number;

    ngOnInit(): void {
        this._eventPromotionsService.promotionChannels.load(this.eventId, this.promotionId);
        this.channelsListData$ = combineLatest([
            this._eventChannelService.eventChannelsList.getData$(),
            this.fixedTypeFilter$.pipe(startWith(null)),
            this._channelsFilters
        ])
            .pipe(
                map(([eventChannels, fixedTypeFilter, filters]) => {
                    let channels = eventChannels?.map(eventChannel => eventChannel.channel) || [];
                    if (fixedTypeFilter || filters.type) {
                        const type = fixedTypeFilter || filters.type;
                        channels = channels.filter(channel => channel.type === type);
                    }
                    if (filters.name) {
                        const name = filters.name.toLowerCase();
                        channels = channels.filter(channel => channel.name.toLowerCase().includes(name));
                    }
                    channels = channels || [];
                    return channels;
                }),
                shareReplay({ refCount: true, bufferSize: 1 })
            );

        this.channelsForm.get('type').valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe((channelsScope: PromotionChannelsScope) =>
                channelsScope === PromotionChannelsScope.restricted ?
                    this.channelsForm.get('selected').enable() : this.channelsForm.get('selected').disable()
            );

        combineLatest([
            this._eventPromotionsService.promotionChannels.get$().pipe(filter(Boolean)),
            this.channelsForm.valueChanges // only used as a trigger
        ])
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(([promChannels]) => {
                FormControlHandler.checkAndRefreshDirtyState(
                    this.channelsForm.get('type'),
                    promChannels.type
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.channelsForm.get('selected'),
                    promChannels.channels?.map(({ id }) => id) || []
                );
                // forces invalid errors checks in template
                this._changeDetRef.markForCheck();
            });

        this._eventPromotionsService.promotionChannels.get$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this._destroyRef)
            )
            .subscribe(promChannels => {
                this.channelsForm.reset({
                    type: promChannels.type,
                    selected: promChannels.channels || []
                });
            });
    }

    filterChannels(type: ChannelType): void {
        type = type || null;
        if (type !== this._channelsFilters.value.type) {
            this._channelsFilters.next({
                type,
                name: this._channelsFilters.value.name
            });
        }
    }

    filterChangeHandler(filters: Partial<PageableFilter>): void {
        this._eventsService.event.get$().pipe(take(1)).subscribe(event => {
            this._eventChannelService.eventChannelsList.clear();
            this._eventChannelService.eventChannelsList.load(event.id, { limit: this.pageSize, ...filters });
        });
    }
}
