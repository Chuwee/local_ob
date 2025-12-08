
import {
    ChannelsService, ChannelType, IsWebChannelVouchersPipe,
    IsWebB2bPipe, IsExternalWhitelabelPipe, IsWebV4$Pipe, IsWebChannelPipe, IsMembersChannelPipe
} from '@admin-clients/cpanel/channels/data-access';
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { ENTITY_SERVICE, LOGIN_CONFIG_SERVICE } from '@admin-clients/cpanel/shared/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonToggleGroup, MatButtonToggle } from '@angular/material/button-toggle';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map } from 'rxjs/operators';

@Component({
    selector: 'app-channel-operative',
    templateUrl: './channel-operative.component.html',
    styleUrls: ['./channel-operative.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, FlexLayoutModule, MatButtonToggleGroup, RouterLink,
        MatButtonToggle, TranslatePipe, RouterOutlet, LastPathGuardListenerDirective,
        IsWebChannelVouchersPipe, IsWebB2bPipe, IsExternalWhitelabelPipe, IsWebV4$Pipe, IsWebChannelPipe, IsMembersChannelPipe
    ],
    providers: [
        { provide: LOGIN_CONFIG_SERVICE, useExisting: ChannelsService },
        { provide: ENTITY_SERVICE, useExisting: EntitiesBaseService }
    ]
})
export class ChannelOperativeComponent implements OnDestroy {
    readonly #auth = inject(AuthenticationService);
    readonly #channelsService = inject(ChannelsService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);

    readonly webBoxOfficeType = ChannelType.webBoxOffice;
    readonly webType = ChannelType.web;

    readonly deepPath$ = getDeepPath$(this.#router, this.#route);
    readonly channel$ = this.#channelsService.getChannel$().pipe(filter(Boolean));
    readonly showLoginConfig$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.CNL_MGR]);
    readonly hideB2BPublishingSection$ = this.#entitiesSrv.getEntity$().pipe(map(entity => !entity?.settings?.allow_B2B_publishing));
    readonly hideDonationsSection$ = this.#entitiesSrv.getEntity$()
        .pipe(map(entity => !entity?.settings?.donations?.some(donation => donation.enabled)));

    readonly isNewReceiptEnabled$ = this.#channelsService.getChannel$().pipe(
        map(channel => !!channel?.settings?.v2_receipt_template_enabled)
    );

    constructor() {
        this.#channelsService.getChannel$()
            .pipe(filter(channel => !!channel?.entity?.id), takeUntilDestroyed())
            .subscribe(channel => {
                this.#entitiesSrv.loadEntity(channel.entity.id);
            });
    }

    ngOnDestroy(): void {
        this.#entitiesSrv.clearEntity();
    }
}
