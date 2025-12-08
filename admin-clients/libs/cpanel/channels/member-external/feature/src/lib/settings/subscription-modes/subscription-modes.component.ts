import { Channel, ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { SubscriptionMode, ChannelMemberExternalService } from '@admin-clients/cpanel-channels-member-external-data-access';
import { MessageDialogService, EphemeralMessageService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { Component, OnInit, ChangeDetectionStrategy, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { first, Observable } from 'rxjs';
import {
    SubscriptionModeDialogComponent as ModeDialog,
    SubscriptionModeDialogData as ModeDialogData
} from './dialog/subscription-mode-dialog.component';

@Component({
    selector: 'app-members-external-subscription-modes',
    templateUrl: './subscription-modes.component.html',
    styleUrls: ['./subscription-modes.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SubscriptionModesComponent implements OnInit {
    readonly #channelsService = inject(ChannelsService);
    readonly #channelMemberSrv = inject(ChannelMemberExternalService);
    readonly #matDialog = inject(MatDialog);
    readonly #msgDialog = inject(MessageDialogService);
    readonly #ephemeralMsg = inject(EphemeralMessageService);

    #channel: Channel;
    columns = ['sid', 'name', 'active', 'actions'];
    subscriptionModes$: Observable<SubscriptionMode[]>;
    loading$ = this.#channelMemberSrv.subscription.list.loading$();
    error$ = this.#channelMemberSrv.subscription.list.error$();

    ngOnInit(): void {
        this.#channelMemberSrv.subscription.list.clear();

        this.subscriptionModes$ = this.#channelMemberSrv.subscription.list.get$();

        this.#channelsService.getChannel$()
            .pipe(first())
            .subscribe(channel => {
                this.#channel = channel;
                this.#channelMemberSrv.subscription.list.load(channel.id);
            });
    }

    toggle(mode: SubscriptionMode, active: boolean): void {
        this.#channelMemberSrv.subscription.update(this.#channel.id, mode.sid, { active } as SubscriptionMode).subscribe(() => {
            this.#ephemeralMsg.showSaveSuccess();
        });
    }

    open(mode?: SubscriptionMode): void {
        this.#matDialog.open<ModeDialog, ModeDialogData, void>(
            ModeDialog,
            new ObMatDialogConfig({ channel: this.#channel, mode })
        );
    }

    delete(mode: SubscriptionMode): void {
        this.#msgDialog.showDeleteConfirmation({
            confirmation: {
                title: 'CHANNELS.MEMBER_EXTERNAL.SUBSCRIPTION_MODES.DELETE_TITLE',
                message: 'CHANNELS.MEMBER_EXTERNAL.SUBSCRIPTION_MODES.DELETE_MESSAGE'
            },
            delete$: this.#channelMemberSrv.subscription.delete(this.#channel.id, mode)
        });

    }

}
