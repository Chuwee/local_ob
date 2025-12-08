import { EventChannel, eventChannelsProviders, EventChannelsService } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { Event } from '@admin-clients/cpanel/promoters/events/data-access';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { InfiniteScrollCustomEvent } from '@ionic/angular';
import { combineLatest, filter, Subject, takeUntil } from 'rxjs';

@Component({
    selector: 'events-channels-tab',
    templateUrl: './channels-tab.component.html',
    styleUrls: ['./channels-tab.component.scss'],
    providers: [eventChannelsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelsTabComponent implements OnInit, OnDestroy {
    readonly #eventChannelsService = inject(EventChannelsService);
    readonly #onDestroy = new Subject<void>();
    readonly #router = inject(Router);
    #offset = 0;
    #limit = 10;

    #requestParams: PageableFilter = {
        offset: this.#offset,
        limit: this.#limit
    };

    @Input() readonly event: Event;
    readonly $isLoading = toSignal(this.#eventChannelsService.eventChannelsList.inProgress$());
    readonly getData$ = combineLatest([
        this.#eventChannelsService.eventChannelsList.getData$(),
        this.#eventChannelsService.eventChannelsList.getMetaData$()
    ]).pipe(
        filter(([eventChannels, metaData]) => !!eventChannels && !!metaData),
        takeUntil(this.#onDestroy)
    ).subscribe(([eventChannels, metaData]) => {
        this.cumulativeChannels = eventChannels;
        this.totalResults = metaData.total;
    });

    totalResults: number;
    cumulativeChannels: EventChannel[] = [];

    ngOnInit(): void {
        this.#eventChannelsService.eventChannelsList.load(this.event.id, this.#requestParams);
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    onIonInfinite(event: InfiniteScrollCustomEvent): void {
        if (this.#offset >= this.totalResults) {
            (event).target.complete();
            return;
        }

        this.increaseParams();
        this.#eventChannelsService.eventChannelsList.load(this.event.id, this.#requestParams);
        if (this.$isLoading()) {
            (event).target.complete();
        }
    }

    goToDetail(channel: EventChannel): void {
        this.#router.navigate(['channel-detail'], {
            queryParams: {
                eventId: this.event.id,
                id: channel.channel.id
            }
        });
    }

    private increaseParams(): void {
        this.#offset += 10;
        this.#requestParams = {
            offset: this.#offset,
            limit: this.#limit
        };
    }
}
