import { Metadata } from '@OneboxTM/utils-state';
import { ChannelSession, ChannelSessionsFilter, ChannelsExtendedService } from '@admin-clients/cpanel/channels/data-access';
import { SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, computed, inject, input, OnDestroy, OnInit, output, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { map, of } from 'rxjs';
import { ReviewConfigSessionsFilterComponent } from './filter/review-config-sessions-filter.component';

const PAGE_SIZE = 5;

@Component({
    selector: 'ob-review-config-sessions',
    templateUrl: './review-config-sessions.component.html',
    styleUrl: './review-config-sessions.component.scss',
    imports: [
        SearchablePaginatedSelectionModule, TranslatePipe, MatTooltip,
        MatIcon, DateTimePipe, MatIconButton, ReviewConfigSessionsFilterComponent
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReviewConfigSessionsComponent implements OnInit, OnDestroy {
    readonly #channelsExtSrv = inject(ChannelsExtendedService);
    readonly #fb = inject(NonNullableFormBuilder);

    readonly $channelId = input.required<number>({ alias: 'channelId' });
    readonly $initSelection = input<ChannelSession[]>([], { alias: 'initSelection' });
    readonly $eventId = input.required<number>({ alias: 'eventId' });
    readonly $selectedChange = output<ChannelSession[]>({ alias: 'selectedChange' });

    #initilizedForm = false;
    #filters: ChannelSessionsFilter = { limit: PAGE_SIZE };
    readonly pageSize = PAGE_SIZE;
    readonly dateTimeFormats = DateTimeFormats;

    readonly sessionsCtrl = this.#fb.control<ChannelSession[]>([]);
    readonly $selectedSessions = toSignal(this.sessionsCtrl.valueChanges, { initialValue: [] });
    readonly $selectedOnly = signal(false);
    readonly sessionsLoading$ = this.#channelsExtSrv.channelSessions.loading$();

    readonly $showSelectedList = computed(() => this.$selectedOnly() && this.$selectedSessions()?.length > 0);
    readonly $sessionsData = computed(() => this.$showSelectedList()
        ? of(this.$selectedSessions())
        : this.#channelsExtSrv.channelSessions.getData$().pipe(map(sessions => sessions || []))
    );

    readonly $sessionsMetadata = computed(() => this.$showSelectedList()
        ? of(new Metadata({ total: this.$selectedSessions()?.length, limit: this.$total(), offset: 0 }))
        : this.#channelsExtSrv.channelSessions.getMetadata$()
    );

    readonly $total = toSignal(this.#channelsExtSrv.channelSessions.getMetadata$().pipe(map(metadata => metadata?.total || 0)));

    ngOnInit(): void {
        this.sessionsCtrl.setValue(this.$initSelection());
        this.#initilizedForm = true;
    }

    ngOnDestroy(): void {
        this.#channelsExtSrv.channelSessions.clear();
    }

    filterChange(filters: ChannelSessionsFilter): void {
        this.#filters = { ...this.#filters, ...filters };
        this.loadSessions();
    }

    loadSessions(): void {
        this.#channelsExtSrv.channelSessions.load(this.$channelId(), this.$eventId(), this.#filters);
    }

    updateSelectedOnly(): void {
        this.$selectedOnly.update(selected => !selected);
    }

    onSelected(data: any): void {
        if (this.#initilizedForm) {
            this.$selectedChange.emit(data);
        }
    }
}
