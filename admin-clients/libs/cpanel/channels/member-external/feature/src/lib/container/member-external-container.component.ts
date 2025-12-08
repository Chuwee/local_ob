import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { ChannelMemberExternalService } from '@admin-clients/cpanel-channels-member-external-data-access';
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, first, map } from 'rxjs/operators';

@Component({
    selector: 'app-channel-member-external-container',
    templateUrl: './member-external-container.component.html',
    styleUrls: ['./member-external-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, FlexLayoutModule, AsyncPipe, TranslatePipe, RouterModule, LastPathGuardListenerDirective
    ]
})
export class MemberExternalContainerComponent implements OnInit {
    #channelsService = inject(ChannelsService);
    #router = inject(Router);
    #route = inject(ActivatedRoute);
    #authSrv = inject(AuthenticationService);
    #channelMemberSrv = inject(ChannelMemberExternalService);

    readonly deepPath$ = getDeepPath$(this.#router, this.#route);
    readonly isOperatorUser$ = this.#authSrv.getLoggedUser$()
        .pipe(
            first(Boolean),
            map(user => AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR]))
        );

    readonly capacitiesMissing$ = this.#channelMemberSrv.channelCapacities.get$().pipe(
        filter(Boolean),
        map(capacities => !capacities.length)
    );

    ngOnInit(): void {
        this.#channelMemberSrv.channelCapacities.clear();
        this.#channelMemberSrv.periodicities.clear();
        this.#channelMemberSrv.roles.clear();
        this.#channelMemberSrv.terms.clear();

        this.#channelsService.getChannel$()
            .pipe(first(Boolean))
            .subscribe(channel => {
                this.#channelMemberSrv.channelCapacities.load(channel.id);
            });
    }

}
