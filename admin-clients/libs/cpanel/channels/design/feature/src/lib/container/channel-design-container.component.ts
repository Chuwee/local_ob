import {
    ChannelsService, ChannelType, IsExternalWhitelabelPipe, IsWebV4$Pipe, IsWebB2bPipe, IsWebChannelPipe, IsV4$Pipe
} from '@admin-clients/cpanel/channels/data-access';
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButtonToggle, MatButtonToggleGroup } from '@angular/material/button-toggle';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, first, map, take, tap } from 'rxjs/operators';

@Component({
    selector: 'app-channel-design-container',
    templateUrl: './channel-design-container.component.html',
    styleUrls: ['./channel-design-container.component.scss'],
    imports: [
        TranslatePipe, RouterOutlet, RouterLink, LastPathGuardListenerDirective, AsyncPipe,
        IsWebV4$Pipe, IsExternalWhitelabelPipe, IsWebChannelPipe, MatButtonToggleGroup, MatButtonToggle, IsWebB2bPipe,
        IsV4$Pipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChannelDesignComponent {
    readonly #channelsService = inject(ChannelsService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #authSrv = inject(AuthenticationService);

    readonly $channel = toSignal(this.#channelsService.getChannel$()
        .pipe(
            filter(Boolean), take(1),
            tap(channel => this.#entitiesSrv.loadEntity(channel.entity.id))
        )
    );

    readonly $deepPath = toSignal(getDeepPath$(this.#router, this.#route));

    readonly $isOperatorUser = toSignal(this.#authSrv.getLoggedUser$()
        .pipe(
            first(Boolean),
            map(user => AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR]))
        ));

    readonly $showCodeEditor = computed(() => {
        const channel = this.$channel();
        const isV4 = channel.settings.v4_config_enabled;
        const isExternalWhitelabel = channel.whitelabel_type === 'EXTERNAL';
        const isOperatorUser = this.$isOperatorUser();
        const isWebChannel = channel.type === ChannelType.web || channel.type === ChannelType.webB2B;
        return (isV4 || isExternalWhitelabel) && isOperatorUser && isWebChannel;
    });
}
