import {
    EventCommunicationService, EventChannelContentImageType as ImageType

} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { Event, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService, SessionsFilterFields, SessionType } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { EventType, ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { LocalDateTimePipe, LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe, KeyValuePipe, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, input, OnDestroy, OnInit } from '@angular/core';
import { MatDivider } from '@angular/material/divider';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { filter, first, map, shareReplay } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        LocalDateTimePipe, LocalNumberPipe, TranslatePipe,
        AsyncPipe, UpperCasePipe, KeyValuePipe, MatDivider
    ],
    selector: 'app-event-principal-info-sidebar',
    styleUrls: ['./event-principal-info-sidebar.component.scss'],
    templateUrl: './event-principal-info-sidebar.component.html'
})
export class EventPrincipalInfoSidebarComponent implements OnInit, OnDestroy {
    readonly #eventCommunicationSrv = inject(EventCommunicationService);
    readonly #eventsSrv = inject(EventsService);
    readonly #sessionsSrv = inject(EventSessionsService);

    readonly dateTimeFormats = DateTimeFormats;
    readonly eventImageUrl$ = combineLatest([
        this.#eventCommunicationSrv.getEventChannelContentImages$(),
        this.#eventsSrv.event.get$()
    ]).pipe(
        filter(values => values.every(Boolean)),
        map(
            ([contents, event]) =>
                contents?.filter(
                    image =>
                        image.type === ImageType.main &&
                        image.language === event?.settings?.languages?.default
                )?.[0]?.image_url
        )
    );

    readonly sessionsResume$ = this.#sessionsSrv.getAllSessions$().pipe(
        first(Boolean),
        map(sessions => ({
            total: sessions.metadata.total,
            capacity: sessions.data.reduce((totalCapacity, session) =>
                totalCapacity + (session.venue_template?.capacity || 0), 0)
        })),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly $event = input(null as Event, { alias: 'event' });
    readonly $isAvet = computed(() => this.$event()?.type === EventType.avet);
    readonly $isSGA = computed(() => this.$event()?.additional_config?.inventory_provider === ExternalInventoryProviders.sga);

    ngOnInit(): void {
        this.#eventCommunicationSrv.loadEventChannelContentImages(
            this.$event().id,
            this.$event().settings.languages.default,
            ImageType.main
        );
        this.#sessionsSrv.loadAllSessions(this.$event().id, {
            sort: `${SessionsFilterFields.startDate}:asc`,
            type: SessionType.session,
            fields: [
                SessionsFilterFields.status,
                SessionsFilterFields.capacity
            ]
        });
    }

    ngOnDestroy(): void {
        this.#eventCommunicationSrv.clearEventChannelContentImages();
        this.#sessionsSrv.clearSessionsState();
    }
}
