import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { EventChannelRequestStatus, EventChannelsService } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { BreadcrumbsService, NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterOutlet } from '@angular/router';
import { filter, map, tap } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [NavTabsMenuComponent, RouterOutlet],
    selector: 'app-event-channel-details',
    templateUrl: './event-channel-details.component.html',
    styleUrls: ['./event-channel-details.component.scss']
})
export class EventChannelDetailsComponent implements OnInit {
    readonly #onDestroy = inject(DestroyRef);
    readonly #route = inject(ActivatedRoute);
    readonly #eventChannelsService = inject(EventChannelsService);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #breadcrumbsService = inject(BreadcrumbsService);

    readonly $requestAccepted = toSignal(this.#eventChannelsService.eventChannel.get$()
        .pipe(map(eventChannel => eventChannel?.status.request === EventChannelRequestStatus.accepted)));

    readonly $eventChannel = toSignal(this.#eventChannelsService.eventChannel.get$().pipe(filter(Boolean)));

    readonly $entity = toSignal(this.#entitiesService.getEntity$());

    readonly channelType = ChannelType;

    get #breadcrumb(): string | undefined {
        return this.#route.snapshot.data['breadcrumb'];
    }

    ngOnInit(): void {
        this.#eventChannelsService.eventChannel.get$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(eventChannel => this.#breadcrumbsService.addDynamicSegment(this.#breadcrumb, eventChannel.channel.name));

        this.#eventChannelsService.eventChannel.inProgress$()
            .pipe(
                filter(Boolean),
                tap(() => this.#breadcrumbsService.addDynamicSegment(this.#breadcrumb, 'LOADING')),
                takeUntilDestroyed(this.#onDestroy)
            ).subscribe();
    }
}
