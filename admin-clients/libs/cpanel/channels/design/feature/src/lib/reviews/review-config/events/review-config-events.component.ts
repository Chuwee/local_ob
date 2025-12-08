import { ChannelEvent, ChannelsExtendedService, GetChannelEventsRequest } from '@admin-clients/cpanel/channels/data-access';
import { SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject, input, OnDestroy, OnInit, output } from '@angular/core';
import { NonNullableFormBuilder } from '@angular/forms';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { map } from 'rxjs';

const PAGE_SIZE = 5;

@Component({
    selector: 'ob-review-config-events',
    templateUrl: './review-config-events.component.html',
    styleUrl: './review-config-events.component.scss',
    imports: [SearchablePaginatedSelectionModule, TranslatePipe, MatTooltipModule],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReviewConfigEventsComponent implements OnInit, OnDestroy {
    readonly #channelsExtSrv = inject(ChannelsExtendedService);
    readonly #fb = inject(NonNullableFormBuilder);

    readonly $channelId = input.required<number>({ alias: 'channelId' });
    readonly $initSelection = input<ChannelEvent[]>([], { alias: 'initSelection' });
    readonly $shouldDisableEvent = input.required({ alias: 'shouldDisableEvent' });
    readonly $selectedChange = output<ChannelEvent[]>({ alias: 'selectedChange' });

    #initilizedForm = false;
    #filters: GetChannelEventsRequest = { q: null, limit: PAGE_SIZE, offset: 0, published: true };
    readonly pageSize = PAGE_SIZE;
    readonly eventsCtrl = this.#fb.control<ChannelEvent[]>([]);

    readonly eventsLoading$ = this.#channelsExtSrv.isChannelEventsLoading$();
    readonly eventsData$ = this.#channelsExtSrv.getChannelEventsData$().pipe(map(events => events || []));
    readonly eventsMetadata$ = this.#channelsExtSrv.getChannelEventsMetadata$();

    ngOnInit(): void {
        this.eventsCtrl.setValue(this.$initSelection());
        this.#initilizedForm = true;
    }

    ngOnDestroy(): void {
        this.#channelsExtSrv.clearChannelEvents();
    }

    loadEvents(filters: GetChannelEventsRequest): void {
        this.#filters = { ...this.#filters, ...filters };
        this.#channelsExtSrv.loadChannelEvents(this.$channelId(), this.#filters);
    }

    selectedEvents(data: any): void {
        if (this.#initilizedForm) {
            this.$selectedChange.emit(data);
        }
    }
}
