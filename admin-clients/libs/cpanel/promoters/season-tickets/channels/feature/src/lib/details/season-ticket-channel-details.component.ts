import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { SeasonTicketChannelsService } from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { CommonModule } from '@angular/common';
import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule,
        NavTabsMenuComponent,
        CommonModule,
        RouterOutlet
    ],
    selector: 'app-season-ticket-channel-details',
    templateUrl: './season-ticket-channel-details.component.html',
    styleUrls: ['./season-ticket-channel-details.component.scss']
})
export class SeasonTicketChannelDetailsComponent {
    readonly #seasonTicketChannelsSrv = inject(SeasonTicketChannelsService);
    readonly #entitiesSrv = inject(EntitiesBaseService);

    readonly requestAccepted$ = this.#seasonTicketChannelsSrv.isSeasonTicketChannelRequestAccepted$();
    readonly $entity = toSignal(this.#entitiesSrv.getEntity$());
    readonly $seasonTicketChannel = toSignal(this.#seasonTicketChannelsSrv.getSeasonTicketChannel$().pipe(filter(Boolean)));

    readonly channelType = ChannelType;
}
