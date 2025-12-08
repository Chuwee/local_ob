import {
    channelOperativeTypes,
    ChannelsService,
    ChannelType,
    channelWebTypes
    , IsV3$Pipe
} from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService, isMultiCurrency$, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { IsItalianEntityPipe } from '@admin-clients/cpanel/organizations/entities/utils';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { GoBackComponent, NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterOutlet } from '@angular/router';
import { filter, map } from 'rxjs';

@Component({
    selector: 'app-details',
    templateUrl: './channel-details.component.html',
    styleUrls: ['./channel-details.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        RouterOutlet,
        GoBackComponent,
        NavTabsMenuComponent,
        IsV3$Pipe,
        AsyncPipe
    ]
})
export class ChannelDetailsComponent implements OnDestroy {
    readonly #channelsService = inject(ChannelsService);
    readonly #entitySrv = inject(EntitiesBaseService);
    readonly #auth = inject(AuthenticationService);
    readonly #isItalianEntityPipe = inject(IsItalianEntityPipe);

    readonly $channel = toSignal(this.#channelsService.getChannel$());
    readonly $isOperatorUser = toSignal(this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]));
    readonly $isSuperOperatorUser = toSignal(this.#auth.hasLoggedUserSomeRoles$([UserRoles.SYS_MGR]));

    // Más adelante, tras la homologación, el flag será más genérico y será back el que haga estas comprobaciones
    readonly $promotionsNotAvailable = toSignal(this.#entitySrv.getEntity$().pipe(
        filter(Boolean),
        map(entity => this.#isItalianEntityPipe.transform(entity))
    ));

    //TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
    readonly $isMultiCurrency = toSignal(isMultiCurrency$());

    // channel types available for each tab
    readonly channelTypesOperative = channelOperativeTypes;
    readonly channelTypesCommunication = channelWebTypes.concat([ChannelType.boxOffice, ChannelType.members]);
    readonly channelTypesDesign = channelWebTypes;
    readonly channelTypesPromotions = [ChannelType.web, ChannelType.webBoxOffice];
    readonly channelTypesPacks = [ChannelType.webB2B, ChannelType.webBoxOffice, ChannelType.boxOffice];
    readonly channelTypesWeb = [ChannelType.web];
    readonly channelTypesNotConfiguration = [ChannelType.external];
    readonly channelTypesMembers = [ChannelType.members];

    ngOnDestroy(): void {
        this.#channelsService.clearChannel();
    }
}
