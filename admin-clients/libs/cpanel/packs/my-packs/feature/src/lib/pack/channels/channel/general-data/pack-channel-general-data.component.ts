import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import {
    getPackSaleStatusIndicator, PackChannel, PackChannelRequestStatus, PacksService
} from '@admin-clients/cpanel/packs/my-packs/data-access';
import { MessageDialogService, DialogSize } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, TranslatePipe, AsyncPipe, NgClass, MatTooltip, MatButton, MatIcon
    ],
    selector: 'app-pack-channel-general-data',
    templateUrl: './pack-channel-general-data.component.html',
    styleUrls: ['./pack-channel-general-data.component.scss']
})
export class PackChannelGeneralDataComponent {
    readonly #packsService = inject(PacksService);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #channelsSrv = inject(ChannelsService);

    readonly $pack = toSignal(this.#packsService.pack.get$());
    readonly $channel = toSignal(this.#channelsSrv.getChannel$());
    readonly packChannel$ = this.#packsService.pack.channel.get$();

    readonly getSaleStatusIndicator = getPackSaleStatusIndicator;
    readonly packChannelRequestStatus = PackChannelRequestStatus;

    requestChannel(packChannel: PackChannel): void {
        this.#messageDialogService.showWarn({
            size: DialogSize.SMALL,
            showCancelButton: true,
            title: 'PACK.CHANNEL.REQUEST',
            message: 'PACK.CHANNEL.REQUEST_MSG',
            messageParams: { channelName: packChannel.channel.name }
        }).subscribe(action => {
            if (action) {
                this.#packsService.pack.channel.request(packChannel.pack.id, packChannel.channel.id)
                    .subscribe(() => this.#packsService.pack.channel.load(packChannel.pack.id, packChannel.channel.id));
            }
        });
    }
}
