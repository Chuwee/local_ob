
import { ImportComContentsGroups } from '@admin-clients/cpanel/channels/communication/data-access';
import { ChannelsService, ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService,
    MessageType,
    NotificationSnackbarData,
    ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, ActivatedRouteSnapshot, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { filter, first, map, tap } from 'rxjs/operators';
import { ChannelCommunicationNotifierService } from './channel-communication-notifier.service';
import { ChannelImportComComponentsDialogComponent } from './import-com-contents/channel-import-com-contents-dialog.component';

@Component({
    selector: 'app-channel-communication-container',
    templateUrl: './channel-communication-container.component.html',
    styleUrls: ['./channel-communication-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [ChannelCommunicationNotifierService],
    standalone: false
})
export class ChannelCommunicationContainerComponent {
    readonly #channelsService = inject(ChannelsService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #auth = inject(AuthenticationService);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #matDialog = inject(MatDialog);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #translate = inject(TranslateService);
    readonly #communicationNotifierService = inject(ChannelCommunicationNotifierService);

    readonly channelTypes = ChannelType;
    readonly webOrWebBoxoffice = [ChannelType.web, ChannelType.webBoxOffice];
    readonly isOperator$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]).pipe(first());
    readonly deepPath$ = getDeepPath$(this.#router, this.#route);
    readonly $showSecondaryMarket = toSignal(this.#entitiesSrv.getEntity$().pipe(map(entity => entity?.settings?.allow_secondary_market)));
    readonly channel$ = this.#channelsService.getChannel$()
        .pipe(
            first(Boolean),
            tap(channel => this.#entitiesSrv.loadEntity(channel.entity.id))
        );

    isNewReceiptEnabled$ = this.#channelsService.getChannel$().pipe(
        map(channel => !!channel?.settings?.v2_receipt_template_enabled)
    );

    readonly showWhatsapp$ =
        this.#entitiesSrv.getEntity$()
            .pipe(
                filter(Boolean),
                map(entity =>
                    entity.settings?.whatsapp?.enabled));

    constructor() {
        this.#checkPath(this.#route.snapshot);
    }

    openImportContentsDialog(): void {
        this.#matDialog.open(ChannelImportComComponentsDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .subscribe((result: { isImported: boolean; errors?: ImportComContentsGroups[] }) => {
                if (result?.isImported) {
                    const data: NotificationSnackbarData = {
                        type: MessageType.success,
                        msgKey: 'CHANNELS.IMPORT_CONTENTS.IMPORT_SUCCESS'
                    };
                    if (result.errors?.length) {
                        data.type = MessageType.alert;
                        data.msgKey = 'CHANNELS.IMPORT_CONTENTS.IMPORT_WARNING';
                        data.msgParams = {
                            erroredGroups: result.errors
                                .map(err => this.#translate.instant('CHANNELS.IMPORT_CONTENTS.GROUP_OPTS.' + err))
                                .join(', ')
                        };
                    }
                    this.#ephemeralMessageService.show(data);
                    this.#communicationNotifierService.sendRefreshDataSignal();
                }
            });
    }

    // guard to control if the route is available for the current channel type
    #checkPath(route: ActivatedRouteSnapshot): void {
        this.#channelsService.getChannel$()
            .pipe(
                first(Boolean),
                takeUntilDestroyed()
            )
            .subscribe(channel => {
                if (!channel.languages?.selected?.length) {
                    this.#router.navigate(['../'], { relativeTo: this.#route });
                } else {
                    const fullpath = route.firstChild?.routeConfig.path ?? '';
                    const path = fullpath.split('/')[0];
                    const subpath = fullpath.split('/')[1];
                    switch (path) {
                        case '':
                            if (channel.type === ChannelType.boxOffice) {
                                this.#router.navigate(['literals', 'email-receipt'], { relativeTo: this.#route });
                            } else {
                                this.#router.navigate(['literals'], { relativeTo: this.#route });
                            }
                            break;
                        case 'literals':
                            if (subpath && channel.type !== ChannelType.boxOffice) {
                                this.#router.navigate(['literals'], { relativeTo: this.#route });
                            } else if (!subpath && channel.type === ChannelType.boxOffice) {
                                this.#router.navigate(['literals', 'email-receipt'], { relativeTo: this.#route });
                            }
                            break;
                    }
                }
            });
    }
}

