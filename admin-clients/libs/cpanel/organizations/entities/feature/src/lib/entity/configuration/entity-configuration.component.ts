import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { ENTITY_SERVICE, LOGIN_CONFIG_SERVICE } from '@admin-clients/cpanel/shared/data-access';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButtonToggle, MatButtonToggleGroup } from '@angular/material/button-toggle';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, filter, first, map } from 'rxjs';

@Component({
    selector: 'app-entity-configuration',
    templateUrl: './entity-configuration.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, TranslatePipe, RouterOutlet, RouterLink, MatButtonToggleGroup,
        MatButtonToggle, LastPathGuardListenerDirective
    ],
    providers: [
        { provide: LOGIN_CONFIG_SERVICE, useExisting: EntitiesService },
        { provide: ENTITY_SERVICE, useExisting: EntitiesService }
    ]
})
export class EntityConfigurationComponent {
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);

    readonly deepPath$ = getDeepPath$(this.#router, this.#route);
    readonly entity$ = this.#entitiesSrv.getEntity$().pipe(filter(entity => !!entity));
    readonly $isSuperOperator = toSignal(this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.SYS_MGR]));

    readonly showWebhooks$ = combineLatest([
        this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.OPR_ANS]),
        this.#authSrv.getLoggedUser$().pipe(first(Boolean)),
        this.entity$
    ]).pipe(map(([isOperator, user, entity]) => !(isOperator && user.entity.id === entity.id)));
}
