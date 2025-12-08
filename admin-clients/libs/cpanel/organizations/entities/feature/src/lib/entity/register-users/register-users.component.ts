import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { ENTITY_SERVICE, LOGIN_CONFIG_SERVICE } from '@admin-clients/cpanel/shared/data-access';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map } from 'rxjs/operators';

@Component({
    imports: [
        AsyncPipe, TranslatePipe, RouterOutlet, MatButtonToggleModule, LastPathGuardListenerDirective, FlexLayoutModule, RouterLink,
        MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle
    ],
    selector: 'app-register-users',
    templateUrl: './register-users.component.html',
    styleUrls: ['./register-users.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        { provide: LOGIN_CONFIG_SERVICE, useExisting: EntitiesService },
        { provide: ENTITY_SERVICE, useExisting: EntitiesService }
    ]
})
export class RegisterUsersComponent {
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #auth = inject(AuthenticationService);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);

    readonly deepPath$ = getDeepPath$(this.#router, this.#route);
    readonly entity$ = this.#entitiesSrv.getEntity$().pipe(filter(entity => !!entity));
    readonly isNotSysAdmin$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.SYS_MGR]).pipe(map(isSysAdmin => !isSysAdmin));
    readonly showLoginConfig$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);
    readonly allowedSecondayMarket$ = this.entity$.pipe(map(entity => !!entity?.settings?.allow_secondary_market));
}
