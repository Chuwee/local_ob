import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { EmptyStateComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, inject, viewChild, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, map } from 'rxjs';
import { EntityZoneTemplatesListComponent } from '../list/entity-zone-templates-list.component';

@Component({
    selector: 'app-entity-zone-templates-container',
    imports: [
        NgClass, MaterialModule, TranslatePipe, FlexModule, FlexLayoutModule, RouterModule,
        EntityZoneTemplatesListComponent, EmptyStateComponent, AsyncPipe
    ],
    templateUrl: './entity-zone-templates-container.component.html',
    styleUrls: ['./entity-zone-templates-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityZoneTemplatesContainerComponent implements OnDestroy, OnInit {
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #authSrv = inject(AuthenticationService);

    readonly zoneTemplates$ = this.#entitiesSrv.zoneTemplates.getData$();
    readonly isLoading$ = this.#entitiesSrv.zoneTemplates.loading$();

    readonly sidebarWidth$: Observable<string> = this.#breakpointObserver
        .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
        .pipe(
            map(result => result.matches ? '240px' : '280px')
        );

    readonly canWrite$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.EVN_MGR]);
    readonly $entity = toSignal(this.#entitiesSrv.getEntity$());

    listComponent = viewChild(EntityZoneTemplatesListComponent);

    ngOnInit(): void {
        this.#entitiesSrv.zoneTemplates.load(this.$entity()?.id, { limit: 999, offset: 0 });
    }

    ngOnDestroy(): void {
        this.#entitiesSrv.zoneTemplates.clear();
    }

    addZoneTemplate(): void {
        this.listComponent().openAddTemplateDialog();
    }
}
