import { UserRoles } from '@admin-clients/cpanel/core/data-access';
import { roleGuard } from '@admin-clients/cpanel/core/utils';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EntityExternalComponent } from './container/entity-external-component.component';
import { EntityExternalMembersComponent } from './members/members.component';
import { ProviderPlanSettingsComponent } from './provider-plan-settings/provider-plan-settings.component';
import { SGAComponent } from './sga/sga.component';
import { SmartBookingComponent } from './smart-booking/smart-booking.component';
import { EntityExternalTicketingComponent } from './ticketing/ticketing.component';

const routes: Routes = [{
    path: '',
    component: EntityExternalComponent,
    canActivate: [roleGuard],
    data: {
        roles: [UserRoles.OPR_MGR]
    },
    children: [
        {
            path: '',
            redirectTo: 'ticketing',
            pathMatch: 'full'
        },
        {
            path: 'ticketing',
            component: EntityExternalTicketingComponent,
            canDeactivate: [unsavedChangesGuard()],
            canActivate: [roleGuard],
            pathMatch: 'full',
            data: {
                breadcrumb: 'ENTITY.EXTERNAL.CONFIGURATION.TITLE',
                roles: [UserRoles.OPR_MGR]
            }
        },
        {
            path: 'members',
            component: EntityExternalMembersComponent,
            canDeactivate: [unsavedChangesGuard()],
            canActivate: [roleGuard],
            pathMatch: 'full',
            data: {
                breadcrumb: 'ENTITY.EXTERNAL.MEMBERS.TITLE',
                roles: [UserRoles.OPR_MGR]
            }
        },
        {
            path: 'smart-booking',
            component: SmartBookingComponent,
            canDeactivate: [unsavedChangesGuard()],
            canActivate: [roleGuard],
            pathMatch: 'full',
            data: {
                breadcrumb: 'ENTITY.EXTERNAL.SMART_BOOKING.TITLE',
                roles: [UserRoles.OPR_MGR]
            }
        },
        {
            path: 'sga',
            component: SGAComponent,
            canDeactivate: [unsavedChangesGuard()],
            canActivate: [roleGuard],
            pathMatch: 'full',
            data: {
                breadcrumb: 'ENTITY.EXTERNAL.SGA.TITLE',
                roles: [UserRoles.OPR_MGR]
            }
        },
        {
            path: 'provider-plan-settings',
            component: ProviderPlanSettingsComponent,
            canDeactivate: [unsavedChangesGuard()],
            canActivate: [roleGuard],
            pathMatch: 'full',
            data: {
                breadcrumb: 'ENTITY.EXTERNAL.PROVIDER_PLAN_SETTINGS.TITLE',
                roles: [UserRoles.OPR_MGR]
            }
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class EntityExternalRoutingModule { }
