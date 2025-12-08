import { Metadata } from '@OneboxTM/utils-state';
import {
    EventChannelsService
} from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { ContentLink, ContentLinkStatus, ContentLinkType } from '@admin-clients/cpanel/shared/data-access';
import { LinkListComponent } from '@admin-clients/cpanel/shared/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, effect, inject, input, OnInit } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { Observable } from 'rxjs';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        LinkListComponent
    ],
    selector: 'app-event-channel-sessions-link-list',
    templateUrl: './event-channel-sessions-link-list.component.html',
    styleUrls: ['./event-channel-sessions-link-list.component.scss']
})
export class EventChannelSessionsLinkListComponent implements OnInit {
    readonly #eventChannelsService = inject(EventChannelsService);
    protected readonly eventChannelSessionsContentLinkType = ContentLinkType;

    publishedSessionLinks$: Observable<ContentLink[]>;
    publishedSessionLinksMetadata$: Observable<Metadata>;
    publishedSessionLinksLoading$: Observable<boolean>;
    unpublishedSessionLinks$: Observable<ContentLink[]>;
    unpublishedSessionLinksMetadata$: Observable<Metadata>;
    unpublishedSessionLinksLoading$: Observable<boolean>;

    readonly pageSize = 10;
    dateTimeFormats = DateTimeFormats;

    readonly $eventId = input.required<number>({ alias: 'eventId' });
    readonly $channelId = input.required<number>({ alias: 'channelId' });
    readonly $language = input.required<string>({ alias: 'language' });

    constructor() {
        effect(() => {
            if (this.$eventId() && this.$channelId() && this.$language()) {
                this.loadSessionsData({ pageIndex: 0 }, this.eventChannelSessionsContentLinkType.published);
                this.loadSessionsData({ pageIndex: 0 }, this.eventChannelSessionsContentLinkType.unpublished);
            }
        });
    }

    ngOnInit(): void {
        this.publishedSessionLinks$ = this.#eventChannelsService.eventChannelPublishedSessionLinks.getData$();
        this.publishedSessionLinksMetadata$ = this.#eventChannelsService.eventChannelPublishedSessionLinks.getMetadata$();
        this.publishedSessionLinksLoading$ = this.#eventChannelsService.eventChannelPublishedSessionLinks.inProgress$();
        this.unpublishedSessionLinks$ = this.#eventChannelsService.eventChannelUnpublishedSessionLinks.getData$();
        this.unpublishedSessionLinksMetadata$ = this.#eventChannelsService.eventChannelUnpublishedSessionLinks.getMetadata$();
        this.unpublishedSessionLinksLoading$ = this.#eventChannelsService.eventChannelUnpublishedSessionLinks.inProgress$();
    }

    loadSessionsData(pageOptions: Partial<PageEvent>, type: ContentLinkType): void {
        const request = {
            eventId: this.$eventId(),
            channelId: this.$channelId(),
            language: this.$language(),
            limit: this.pageSize,
            sort: 'start_date:asc',
            offset: this.pageSize * pageOptions.pageIndex,
            session_status: [ContentLinkStatus.preview]
        };
        if (type === ContentLinkType.published) {
            request.session_status = [ContentLinkStatus.ready, ContentLinkStatus.scheduled];
            this.#eventChannelsService.eventChannelPublishedSessionLinks.load(request);
        } else {
            this.#eventChannelsService.eventChannelUnpublishedSessionLinks.load(request);
        }
    }
}
