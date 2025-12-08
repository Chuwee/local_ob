import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { ENTITY_SERVICE, LOGIN_CONFIG_SERVICE } from '@admin-clients/cpanel/shared/data-access';
import { EntityStatus } from '@admin-clients/shared/common/data-access';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonToggle, MatButtonToggleGroup } from '@angular/material/button-toggle';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, combineLatest } from 'rxjs';
import { filter, first, map } from 'rxjs/operators';

@Component({
    selector: 'app-entity-general-data',
    templateUrl: './entity-general-data.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, TranslatePipe, RouterOutlet, RouterLink, MatButtonToggleGroup,
        MatButtonToggle, LastPathGuardListenerDirective, FlexLayoutModule
    ],
    providers: [
        { provide: LOGIN_CONFIG_SERVICE, useExisting: EntitiesService },
        { provide: ENTITY_SERVICE, useExisting: EntitiesService }
    ]
})
export class EntityGeneralDataComponent {
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #auth = inject(AuthenticationService);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);

    readonly deepPath$ = getDeepPath$(this.#router, this.#route);
    readonly entity$ = this.#entitiesSrv.getEntity$().pipe(filter(entity => !!entity));
    readonly isOperator$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.OPR_ANS]);
    readonly isNotSysAdmin$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.SYS_MGR]).pipe(map(isSysAdmin => !isSysAdmin));
    readonly isOperatorEntity$ = this.entity$.pipe(map(entity => entity.id === entity.operator.id));
    readonly isNotEntityAdmin$ = this.entity$.pipe(map(entity => !entity.settings.types.includes('ENTITY_ADMIN')));
    readonly entityStatusList = EntityStatus;
    readonly showManagementOptions$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.ENT_MGR, UserRoles.OPR_MGR]);
    readonly showCustomersLogin$ = this.entity$.pipe(map(entity => entity.settings.allow_members));
    readonly showManagedEntities$ = combineLatest([
        this.entity$.pipe(map(entity => entity.settings.types.includes('ENTITY_ADMIN'))),
        this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR])
    ]).pipe(map(([isEntityAdmin, isOperatorManager]) => isEntityAdmin && isOperatorManager));

    readonly showWebhooks$ = combineLatest([
        this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.OPR_ANS]),
        this.#auth.getLoggedUser$().pipe(first(Boolean)),
        this.entity$
    ]).pipe(map(([isOperator, user, entity]) => !(isOperator && user.entity.id === entity.id)));

    readonly updateStatus = (id, status): Observable<void> => this.#entitiesSrv.updateEntity(id, { status });
}
