import { ChannelsService, ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { PackChannelRequestStatus, PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import {
    NavTabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { RouterOutlet } from '@angular/router';
import { combineLatest, filter, map } from 'rxjs';

@Component({
    selector: 'app-pack-channel-details',
    imports: [
        RouterOutlet, MatProgressSpinner, NavTabsMenuComponent, AsyncPipe
    ],
    templateUrl: './pack-channel-details.component.html',
    styleUrls: ['./pack-channel-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackChannelDetailsComponent implements OnDestroy {
    readonly #packsSrv = inject(PacksService);
    readonly #channelsSrv = inject(ChannelsService);

    readonly isInProgress$ = this.#packsSrv.pack.channels.loading$();

    readonly selectedPackChannel$ = combineLatest([
        this.#packsSrv.pack.channels.getData$(),
        this.#channelsSrv.getChannel$()
    ]).pipe(
        filter(data => data.every(Boolean)),
        map(([channels, currentChannel]) => channels?.find(channel => channel.channel.id === currentChannel.id))
    );

    readonly $pack = toSignal(this.#packsSrv.pack.get$());

    readonly $isNotBoxOfficeChannel = toSignal(this.selectedPackChannel$
        .pipe(filter(Boolean), map(element => element?.channel?.type !== ChannelType.boxOffice)));

    packChannelRequestStatus = PackChannelRequestStatus;

    ngOnDestroy(): void {
        this.#packsSrv.pack.channels.clear();
        this.#channelsSrv.channelsList.clear();
        this.#channelsSrv.clearChannel();
    }
}
