import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { AdminChannelsService } from '@admin-clients/cpanel/migration/channels/data-access';
import {
    DialogSize, EmptyStateComponent, EphemeralMessageService, MessageDialogService, openDialog
} from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import { MatList, MatListItem, MatListItemMeta, MatListItemTitle } from '@angular/material/list';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSlideToggle, MatSlideToggleChange } from '@angular/material/slide-toggle';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { first, map, of, switchMap } from 'rxjs';
import { AdminChannelCorsDialogComponent } from './dialog/admin-channel-cors-dialog.component';

const ORIGINS_LIMIT = 10;

@Component({
    selector: 'app-admin-channel-cors',
    templateUrl: './admin-channel-cors.component.html',
    styleUrl: './admin-channel-cors.component.scss',
    imports: [
        FormContainerComponent, MatSlideToggle, MatTooltip, MatList, MatListItem, MatListItemTitle, MatListItemMeta,
        TranslatePipe, MatDivider, MatButton, MatIconButton, MatIcon, MatProgressSpinner, EmptyStateComponent
    ],
    providers: [AdminChannelsService, ChannelsService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminChannelCorsComponent {
    #channelId: number;

    readonly #dialogSrv = inject(MatDialog);
    readonly #adminChannelSrv = inject(AdminChannelsService);
    readonly #channelSrv = inject(ChannelsService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);

    readonly originsLimit = ORIGINS_LIMIT;
    readonly $isInProgress = toSignal(this.#adminChannelSrv.channelCorsSettings.loading$());
    readonly $settings = toSignal(this.#adminChannelSrv.channelCorsSettings.get$());

    constructor() {
        this.#channelSrv.getChannel$()
            .pipe(first(Boolean))
            .subscribe(channel => {
                this.#channelId = channel.id;
                this.#adminChannelSrv.channelCorsSettings.load(channel.id);
            });
    }

    openCreateOrEditCorsDialog(originIndex: number | null = null): void {
        openDialog(
            this.#dialogSrv,
            AdminChannelCorsDialogComponent,
            {
                channelId: this.#channelId,
                settings: this.$settings(),
                originIndex
            })
            .afterClosed()
            .subscribe(result => {
                if (result) {
                    this.#adminChannelSrv.channelCorsSettings.load(this.#channelId);
                    (originIndex === null) ? this.#ephemeralMsgSrv.showCreateSuccess() : this.#ephemeralMsgSrv.showSaveSuccess();
                }
            });
    }

    openDeleteCorsDialog(originIndex: number): void {
        this.#msgDialogSrv
            .showWarn({
                size: DialogSize.SMALL,
                title: 'ADMIN_CHANNEL.CONFIGURATION.CORS.ACTIONS.DELETE_TITLE',
                message: 'ADMIN_CHANNEL.CONFIGURATION.CORS.ACTIONS.DELETE_MESSAGE',
                messageParams: { origin: this.$settings().allowed_origins[originIndex] },
                actionLabel: 'FORMS.ACTIONS.DELETE',
                showCancelButton: true
            })
            .pipe(
                switchMap(success => {
                    if (success) {
                        const updatedOrigins = this.$settings().allowed_origins.filter((_, index) => index !== originIndex);
                        const updatedSettings = { enabled: this.$settings().enabled, allowed_origins: updatedOrigins };

                        return this.#adminChannelSrv.channelCorsSettings.upsert$(this.#channelId, updatedSettings).pipe(map(() => true));
                    }

                    return of(false);
                })
            )
            .subscribe(result => {
                if (result) {
                    this.#adminChannelSrv.channelCorsSettings.load(this.#channelId);
                    this.#ephemeralMsgSrv.showDeleteSuccess();
                }
            });
    }

    updateEnabled(change: MatSlideToggleChange): void {
        const updatedSettings = {
            allowed_origins: this.$settings().allowed_origins,
            enabled: change.checked
        };

        this.#adminChannelSrv.channelCorsSettings.upsert$(this.#channelId, updatedSettings)
            .subscribe(() => this.#ephemeralMsgSrv.showSaveSuccess());
    }
}
