import { Metadata } from '@OneboxTM/utils-state';
import { GetChannelsRequest, ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { NotificationChannelsScope } from '@admin-clients/cpanel/notifications/data-access';
import { EventChannelsService, eventChannelsProviders } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { OrderChannel } from '@admin-clients/cpanel-sales-data-access';
import { SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { NgFor, NgIf, KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ExtendedModule } from '@angular/flex-layout/extended';
import { FlexModule } from '@angular/flex-layout/flex';
import { UntypedFormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import { distinctUntilChanged, filter, map, shareReplay, takeUntil } from 'rxjs/operators';

const PAGE_SIZE = 10;

@Component({
    selector: 'app-notification-channels',
    templateUrl: './notification-channels.component.html',
    styleUrls: ['./notification-channels.component.scss'],
    providers: [
        eventChannelsProviders
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexModule, FormsModule, ReactiveFormsModule, MaterialModule,
        ExtendedModule, SearchablePaginatedSelectionModule, NgFor, NgIf,
        TranslatePipe, KeyValuePipe
    ]
})
export class NotificationChannelsComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _filters: GetChannelsRequest = { limit: PAGE_SIZE, offset: 0 };
    private _eventId: number;

    @Input() eventId$: Observable<number>;
    @Input() channelsForm: UntypedFormGroup;
    @Input() entityId: number;

    readonly pageSize = PAGE_SIZE;
    readonly notificationChannelsScope = NotificationChannelsScope;
    readonly channelType = ChannelType;

    channelsListMetadata$: Observable<Metadata>;
    channelsListData$: Observable<OrderChannel[]>;
    loading$: Observable<boolean>;
    hasSelectableChannels$: Observable<boolean>;

    constructor(
        private _eventChannelService: EventChannelsService
    ) { }

    ngOnInit(): void {
        this.eventId$.pipe(filter(Boolean)).subscribe(id => {
            this._eventId = id;
            const params = {
                ...this._filters,
                entity_id: this.entityId
            };
            this._eventChannelService.eventChannelsList.load(this._eventId, params);
        });

        this.channelsListData$ = this._eventChannelService.eventChannelsList.getData$().pipe(
            filter(data => !!data),
            map(data => data.map(eventChannel => eventChannel.channel))
        );
        this.channelsListMetadata$ = this._eventChannelService.eventChannelsList.getMetaData$();

        this.channelsForm.get('type').valueChanges
            .pipe(distinctUntilChanged(), takeUntil(this._onDestroy))
            .subscribe((scope: NotificationChannelsScope) => {
                scope === NotificationChannelsScope.restricted ?
                    this.channelsForm.get('selected').enable() : this.channelsForm.get('selected').disable();
            });

        this.loading$ = this._eventChannelService.eventChannelsList.inProgress$();

        this.hasSelectableChannels$ = this.channelsListData$
            .pipe(
                map(channels => !!channels?.length),
                shareReplay(1)
            );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    loadChannelsList({ limit, q, offset }: SearchablePaginatedSelectionLoadEvent): void {
        this._filters = { ...this._filters, limit, offset, q: q?.length ? q : null };
        const params = {
            ...this._filters,
            entity_id: this.entityId
        };
        if (this._eventId) {
            this._eventChannelService.eventChannelsList.load(this._eventId, params);
        }
    }

    checkElement(id: number): boolean {
        return this.channelsForm.value.selected?.find(e => e.id === id) !== undefined;
    }

    filterChannels(type: ChannelType): void {
        this._filters = { ...this._filters, type: type || null };
        const params = {
            ...this._filters,
            entity_id: this.entityId
        };
        this._eventChannelService.eventChannelsList.load(this._eventId, params);
    }
}
