import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { GoBackComponent, NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, computed, inject, OnDestroy } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ActivatedRoute, RouterOutlet } from '@angular/router';

@Component({
    selector: 'app-entity-details',
    templateUrl: './entity-details.component.html',
    styleUrls: ['./entity-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        RouterOutlet,
        GoBackComponent,
        FlexLayoutModule,
        NavTabsMenuComponent
    ]
})
export class EntityDetailsComponent implements OnDestroy {
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #route = inject(ActivatedRoute);
    readonly #authSrv = inject(AuthenticationService);

    readonly isMyEntityUrl = !!this.#route.snapshot.params['entityId'];
    readonly $entity = toSignal(this.#entitiesSrv.getEntity$());
    readonly $isOperator = toSignal(this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]));
    readonly $isSuperOperator = toSignal(this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.SYS_MGR]));

    readonly $isNotEntityAdmin = computed(() => !this.$entity()?.settings?.types?.includes('ENTITY_ADMIN'));
    readonly $isOperatorEntity = computed(() => this.$entity()?.id === this.$entity()?.operator?.id);
    readonly $isExternal = computed(() => this.$entity()?.settings?.allow_avet_integration && !this.$isOperatorEntity());

    ngOnDestroy(): void {
        this.#entitiesSrv.clearEntity();
    }
}
