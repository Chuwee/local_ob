import { ChannelsService, IsBoxOfficePipe, IsMembersChannelPipe, IsWebChannelPipe } from '@admin-clients/cpanel/channels/data-access';
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButtonToggle, MatButtonToggleGroup } from '@angular/material/button-toggle';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter } from 'rxjs/operators';

@Component({
    selector: 'app-channel-configuration-container',
    templateUrl: './channel-configuration-container.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatButtonToggleGroup, MatButtonToggle, TranslatePipe,
        IsWebChannelPipe, IsMembersChannelPipe, RouterLink, RouterOutlet,
        LastPathGuardListenerDirective, IsBoxOfficePipe
    ]
})
export class ChannelConfigurationComponent {
    readonly #channelsSrv = inject(ChannelsService);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #auth = inject(AuthenticationService);

    readonly $channel = toSignal(this.#channelsSrv.getChannel$().pipe(filter(Boolean)));
    readonly $deepPath = toSignal(getDeepPath$(this.#router, this.#route));
    readonly $isOperatorUser = toSignal(this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]));
}
