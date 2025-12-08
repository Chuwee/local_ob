import { UserRoles } from '@admin-clients/cpanel/core/data-access';
import { roleGuard } from '@admin-clients/cpanel/core/utils';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ApiManagementComponent } from './api-management/api-management.component';
import { EntityUserPermissionsComponent } from './entity-user-permissions.component';
import { RolesAndPermissionsComponent } from './roles-and-permissions/roles-and-permissions.component';

const routes: Routes = [{
    path: '',
    component: EntityUserPermissionsComponent,
    children: [
        {
            path: '',
            pathMatch: 'full',
            redirectTo: 'roles'
        },
        {
            path: 'roles',
            component: RolesAndPermissionsComponent,
            canDeactivate: [unsavedChangesGuard()],
            pathMatch: 'full',
            data: {
                breadcrumb: 'USER.USER_ROLES_TITLE'
            }
        },
        {
            path: 'api-management',
            component: ApiManagementComponent,
            canDeactivate: [unsavedChangesGuard()],
            canActivate: [roleGuard],
            pathMatch: 'full',
            data: {
                breadcrumb: 'USER.TITLES.API_MANAGEMENT',
                roles: [UserRoles.SYS_MGR, UserRoles.SYS_ANS]
            }
        },
        {
            path: 'rate-limit',
            loadComponent: () => import('./rate-limit/rate-limit.component').then(m => m.RateLimitComponent),
            canActivate: [roleGuard],
            pathMatch: 'full',
            data: {
                breadcrumb: 'USER.TITLES.RATE_LIMIT',
                roles: [UserRoles.SYS_MGR, UserRoles.SYS_ANS]
            }
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class EntityUserPermissionsRoutingModule { }
